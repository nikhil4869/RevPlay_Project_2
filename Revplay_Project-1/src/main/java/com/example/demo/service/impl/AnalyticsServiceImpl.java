package com.example.demo.service.impl;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.Song;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.service.AnalyticsService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.dto.analytics.DailyTrendDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

	private final UserRepository userRepository;
	private final SongRepository songRepository;
	private final FavoriteRepository favoriteRepository;
	private final PlayHistoryRepository playHistoryRepository;
	private final AlbumRepository albumRepository;
	private static final Logger logger = LogManager.getLogger(AnalyticsServiceImpl.class);
	public AnalyticsServiceImpl(UserRepository userRepository, SongRepository songRepository,
			FavoriteRepository favoriteRepository, PlayHistoryRepository playHistoryRepository,
			AlbumRepository albumRepository) {

		this.userRepository = userRepository;
		this.songRepository = songRepository;
		this.favoriteRepository = favoriteRepository;
		this.playHistoryRepository = playHistoryRepository;
		this.albumRepository = albumRepository;
	}

	@Override
	public ArtistAnalyticsDTO getArtistAnalytics() {

	    logger.debug("Fetching artist analytics");

	    String email = SecurityUtil.getCurrentUserEmail();

	    User artist = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.error("Artist not found with email: {}", email);
	                return new RuntimeException("Artist not found");
	            });

	    long totalSongs = songRepository.countByArtist(artist);
	    long totalAlbums = albumRepository.countByArtist(artist);
	    long totalPlays = songRepository.sumPlayCountByArtist(artist);
	    long totalFavorites = favoriteRepository.countBySongArtist(artist);

	    var topSongs = songRepository
	            .findTop5ByArtistOrderByPlayCountDesc(artist)
	            .stream()
	            .map(Song::getTitle)
	            .collect(Collectors.toList());

	    logger.info("Artist analytics generated. Artist id: {}, Songs: {}, Albums: {}, Plays: {}, Favorites: {}",
	            artist.getId(), totalSongs, totalAlbums, totalPlays, totalFavorites);

	    return new ArtistAnalyticsDTO(totalSongs, totalAlbums, totalPlays, totalFavorites, topSongs);
	}

	@Override
	public List<SongPlayChartDTO> getPlayChart() {

	    logger.debug("Fetching play chart data");

	    String email = SecurityUtil.getCurrentUserEmail();

	    User artist = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.error("Artist not found with email: {}", email);
	                return new RuntimeException("Artist not found");
	            });

	    List<SongPlayChartDTO> chart = songRepository.findByArtist(artist).stream()
	            .map(song -> new SongPlayChartDTO(song.getTitle(), song.getPlayCount()))
	            .toList();

	    logger.info("Play chart generated for artist id: {}. Total songs: {}",
	            artist.getId(), chart.size());

	    return chart;
	}

	@Override
	public ListenerInsightsDTO getListenerInsights() {

	    logger.debug("Fetching listener insights");

	    String email = SecurityUtil.getCurrentUserEmail();

	    User artist = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.error("Artist not found with email: {}", email);
	                return new RuntimeException("Artist not found");
	            });

	    long listeners = playHistoryRepository.countUniqueListeners(artist);

	    logger.info("Listener insights generated for artist id: {}. Unique listeners: {}",
	            artist.getId(), listeners);

	    return new ListenerInsightsDTO(listeners);
	}
	@Override
	public List<DailyTrendDTO> getDailyTrends() {

	    logger.debug("Fetching daily trends");

	    String email = SecurityUtil.getCurrentUserEmail();

	    User artist = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.error("Artist not found with email: {}", email);
	                return new RuntimeException("Artist not found");
	            });

	    List<DailyTrendDTO> trends = playHistoryRepository
	            .getDailyPlayCounts(artist.getId())
	            .stream()
	            .map(row -> new DailyTrendDTO(
	                    ((java.time.LocalDateTime) row[0]).toLocalDate(),
	                    ((Number) row[1]).longValue()))
	            .toList();

	    logger.info("Daily trends generated for artist id: {}. Total days: {}",
	            artist.getId(), trends.size());

	    return trends;
	}
}