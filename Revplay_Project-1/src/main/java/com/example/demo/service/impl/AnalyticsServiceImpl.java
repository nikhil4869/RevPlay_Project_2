package com.example.demo.service.impl;

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


@Service
public class AnalyticsServiceImpl implements AnalyticsService {

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
}