package com.example.demo.service.impl;

<<<<<<< HEAD
import com.example.demo.dto.analytics.UserAnalyticsDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.AnalyticsService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
=======
import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.Song;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.service.AnalyticsService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.dto.analytics.DailyTrendDTO;

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

<<<<<<< HEAD
    private final ListeningHistoryRepository historyRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public AnalyticsServiceImpl(ListeningHistoryRepository historyRepository,
                                FavoriteRepository favoriteRepository,
                                PlaylistRepository playlistRepository,
                                UserRepository userRepository,
                                SongRepository songRepository) {
        this.historyRepository = historyRepository;
        this.favoriteRepository = favoriteRepository;
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    // ===================== FULL ANALYTICS DASHBOARD =====================

    @Override
    public UserAnalyticsDTO getMyAnalytics() {

        User listener = getCurrentUser();

        List<ListeningHistory> history =
                historyRepository.findByListenerOrderByPlayedAtDesc(listener);

        UserAnalyticsDTO dto = new UserAnalyticsDTO();

        dto.setTotalPlays(history.size());
        dto.setFavoriteCount(favoriteRepository.findByListener(listener).size());
        dto.setPlaylistCount(playlistRepository.findByListener(listener).size());

        if (!history.isEmpty()) {

            // Most Played Song
            Map<Song, Long> songCount =
                    history.stream()
                           .collect(Collectors.groupingBy(
                               ListeningHistory::getSong,
                               Collectors.counting()
                           ));

            Song mostPlayedSong =
                    songCount.entrySet()
                             .stream()
                             .max(Map.Entry.comparingByValue())
                             .get()
                             .getKey();

            dto.setMostPlayedSong(mostPlayedSong.getTitle());

            // Most Played Genre
            Map<String, Long> genreCount =
                    history.stream()
                           .collect(Collectors.groupingBy(
                               h -> h.getSong().getGenre(),
                               Collectors.counting()
                           ));

            String mostPlayedGenre =
                    genreCount.entrySet()
                              .stream()
                              .max(Map.Entry.comparingByValue())
                              .get()
                              .getKey();

            dto.setMostPlayedGenre(mostPlayedGenre);
        }

        return dto;
    }

    // ===================== SUMMARY =====================

    @Override
    public Map<String, Object> getListenerSummary() {

        User listener = getCurrentUser();

        List<ListeningHistory> history =
                historyRepository.findByListenerOrderByPlayedAtDesc(listener);

        Map<String, Object> summary = new HashMap<>();

        summary.put("totalPlays", history.size());

        summary.put("uniqueSongsPlayed",
                history.stream()
                       .map(h -> h.getSong().getId())
                       .distinct()
                       .count());

        summary.put("favoriteCount",
                favoriteRepository.findByListener(listener).size());

        summary.put("playlistCount",
                playlistRepository.findByListener(listener).size());

        return summary;
    }

    // ===================== TOP SONGS =====================

    @Override
    public List<SongDTO> getTopSongs(int limit) {

        return songRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Song::getPlayCount).reversed())
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ===================== TOP GENRES =====================

    @Override
    public List<Map<String, Object>> getTopGenres() {

        List<Song> songs = songRepository.findAll();

        Map<String, Long> genreCount =
                songs.stream()
                     .collect(Collectors.groupingBy(
                         Song::getGenre,
                         Collectors.summingLong(Song::getPlayCount)
                     ));

        return genreCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("genre", entry.getKey());
                    map.put("plays", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ===================== HELPER METHODS =====================

    private User getCurrentUser() {

        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private SongDTO mapToDTO(Song song) {

        return new SongDTO(
                song.getId(),
                song.getTitle(),
                song.getGenre(),
                song.getDuration(),
                song.getAudioPath(),
                song.getCoverImage(),
                song.getArtist().getName()
        );
    }
=======
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlayHistoryRepository playHistoryRepository;



    public AnalyticsServiceImpl(UserRepository userRepository, SongRepository songRepository,
			FavoriteRepository favoriteRepository, PlayHistoryRepository playHistoryRepository) {
		this.userRepository = userRepository;
		this.songRepository = songRepository;
		this.favoriteRepository = favoriteRepository;
		this.playHistoryRepository = playHistoryRepository;
	}

	@Override
    public ArtistAnalyticsDTO getArtistAnalytics() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        long totalSongs = songRepository.countByArtist(artist);

        long totalPlays = songRepository.sumPlayCountByArtist(artist);

        long totalFavorites = favoriteRepository.countBySongArtist(artist);

        var topSongs = songRepository
                .findTop5ByArtistOrderByPlayCountDesc(artist)
                .stream()
                .map(Song::getTitle)
                .collect(Collectors.toList());

        return new ArtistAnalyticsDTO(
                totalSongs,
                totalPlays,
                totalFavorites,
                topSongs
        );
    }
    
    @Override
    public List<SongPlayChartDTO> getPlayChart() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return songRepository.findByArtist(artist)
                .stream()
                .map(song -> new SongPlayChartDTO(
                        song.getTitle(),
                        song.getPlayCount()
                ))
                .toList();
    }
    
    @Override
    public ListenerInsightsDTO getListenerInsights() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        long listeners = playHistoryRepository.countUniqueListeners(artist);

        return new ListenerInsightsDTO(listeners);
    }
    
    @Override
    public List<DailyTrendDTO> getDailyTrends() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return playHistoryRepository.getDailyPlayCounts(artist.getId())
                .stream()
                .map(row -> new DailyTrendDTO(
                        ((java.time.LocalDateTime) row[0]).toLocalDate(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}