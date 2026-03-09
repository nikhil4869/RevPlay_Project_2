package com.example.demo.service.impl;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.Song;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.service.AnalyticsService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.DailyTrendDTO;
import com.example.demo.dto.analytics.UserAnalyticsDTO;
import com.example.demo.repository.PlaylistRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

	private static final Logger logger = LogManager.getLogger(AnalyticsServiceImpl.class);

	private final UserRepository userRepository;
	private final SongRepository songRepository;
	private final FavoriteRepository favoriteRepository;
	private final PlayHistoryRepository playHistoryRepository;
	private final AlbumRepository albumRepository;
	private final PlaylistRepository playlistRepository;

	public AnalyticsServiceImpl(UserRepository userRepository, SongRepository songRepository,
			FavoriteRepository favoriteRepository, PlayHistoryRepository playHistoryRepository,
			AlbumRepository albumRepository, PlaylistRepository playlistRepository) {

		this.userRepository = userRepository;
		this.songRepository = songRepository;
		this.favoriteRepository = favoriteRepository;
		this.playHistoryRepository = playHistoryRepository;
		this.albumRepository = albumRepository;
		this.playlistRepository = playlistRepository;

		logger.info("AnalyticsServiceImpl initialized");
	}

	@Override
	public ArtistAnalyticsDTO getArtistAnalytics() {

		logger.info("Fetching artist analytics");

		String email = SecurityUtil.getCurrentUserEmail();

		logger.debug("Current artist email={}", email);

		User artist = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Artist not found"));

		long totalSongs = songRepository.countByArtist(artist);

		long totalAlbums = albumRepository.countByArtist(artist);

		long totalPlays = songRepository.sumPlayCountByArtist(artist);

		long totalFavorites = favoriteRepository.countBySongArtist(artist);

		logger.debug("Artist stats calculated songs={}, albums={}, plays={}, favorites={}",
				totalSongs, totalAlbums, totalPlays, totalFavorites);

		var topSongs = songRepository.findTop5ByArtistOrderByPlayCountDesc(artist).stream()
				.map(Song::getTitle)
				.collect(Collectors.toList());

		logger.info("Artist analytics generated successfully");

		return new ArtistAnalyticsDTO(totalSongs, totalAlbums, totalPlays, totalFavorites, topSongs);
	}

	@Override
	public List<SongPlayChartDTO> getPlayChart() {

		logger.info("Fetching play chart for artist");

		String email = SecurityUtil.getCurrentUserEmail();

		User artist = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Artist not found"));

		List<SongPlayChartDTO> chart = songRepository.findByArtist(artist).stream()
				.map(song -> new SongPlayChartDTO(song.getTitle(), song.getPlayCount()))
				.toList();

		logger.info("Play chart generated with {} songs", chart.size());

		return chart;
	}

	@Override
	public ListenerInsightsDTO getListenerInsights() {

		logger.info("Fetching listener insights");

		String email = SecurityUtil.getCurrentUserEmail();

		User artist = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Artist not found"));

		long listeners = playHistoryRepository.countUniqueListeners(artist);

		logger.info("Total unique listeners={}", listeners);

		return new ListenerInsightsDTO(listeners);
	}

	@Override
	public UserAnalyticsDTO getUserAnalytics() {

		logger.info("Fetching user analytics");

		String email = SecurityUtil.getCurrentUserEmail();

		logger.debug("Current user email={}", email);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		long favorites = favoriteRepository.countByUser(user);
		long playlists = playlistRepository.countByUser(user);

		logger.debug("User stats favorites={} playlists={}", favorites, playlists);

		long totalSeconds = 0;

		try {
			totalSeconds = playHistoryRepository.getTotalListeningTime(user);
		} catch (Exception e) {
			logger.warn("Failed to fetch listening time, using fallback");
		}

		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;

		String listeningTime = hours + " hr " + minutes + " min";

		List<Map<String, Object>> topSongs = new ArrayList<>();

		try {
			topSongs = playHistoryRepository.findMostPlayedSongs(user).stream()
					.limit(5)
					.filter(row -> row != null && row.length >= 3)
					.map(row -> {
						Map<String, Object> map = new HashMap<>();
						map.put("title", row[1] != null ? row[1] : "Unknown");
						map.put("count", row[2] != null ? row[2] : 0L);
						return map;
					})
					.collect(Collectors.toList());

			logger.debug("Top songs calculated count={}", topSongs.size());

		} catch (Exception e) {
			logger.warn("Failed to fetch top songs");
		}

		List<Object[]> history = new ArrayList<>();

		try {

			history = playHistoryRepository.findByUserOrderByPlayedAtDesc(user).stream()
					.limit(200)
					.filter(ph -> ph != null && ph.getSong() != null)
					.map(ph -> {
						String artistName = (ph.getSong().getArtist() != null)
								? ph.getSong().getArtist().getName()
								: "Unknown";

						String genreName = ph.getSong().getGenre();

						return new Object[]{artistName, genreName};
					})
					.collect(Collectors.toList());

			logger.debug("History records processed={}", history.size());

		} catch (Exception e) {
			logger.warn("Failed to process listening history");
		}

		Map<String, Long> artistCounts = history.stream()
				.filter(row -> row[0] != null)
				.collect(Collectors.groupingBy(row -> (String) row[0], Collectors.counting()));

		List<Map<String, Object>> topArtists = artistCounts.entrySet().stream()
				.sorted((a, b) -> b.getValue().compareTo(a.getValue()))
				.limit(5)
				.map(e -> {
					Map<String, Object> map = new HashMap<>();
					map.put("name", e.getKey());
					map.put("count", e.getValue());
					return map;
				})
				.collect(Collectors.toList());

		Map<String, Long> genreCounts = history.stream()
				.filter(row -> row[1] != null)
				.collect(Collectors.groupingBy(row -> (String) row[1], Collectors.counting()));

		List<Map<String, Object>> topGenres = genreCounts.entrySet().stream()
				.sorted((a, b) -> b.getValue().compareTo(a.getValue()))
				.limit(5)
				.map(e -> {
					Map<String, Object> map = new HashMap<>();
					map.put("genre", e.getKey());
					map.put("count", e.getValue());
					return map;
				})
				.collect(Collectors.toList());

		logger.info("User analytics generated successfully");

		return new UserAnalyticsDTO(favorites, playlists, listeningTime, topSongs, topArtists, topGenres);
	}

	@Override
	public List<DailyTrendDTO> getDailyTrends() {

		logger.info("Fetching daily play trends");

		String email = SecurityUtil.getCurrentUserEmail();

		User artist = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Artist not found"));

		List<DailyTrendDTO> trends = playHistoryRepository.getDailyPlayCounts(artist.getId()).stream()
				.map(row -> new DailyTrendDTO(((java.time.LocalDateTime) row[0]).toLocalDate(),
						((Number) row[1]).longValue()))
				.toList();

		logger.info("Daily trends generated count={}", trends.size());

		return trends;
	}
}