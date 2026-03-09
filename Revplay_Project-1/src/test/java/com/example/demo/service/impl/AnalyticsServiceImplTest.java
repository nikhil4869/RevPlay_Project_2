package com.example.demo.service.impl;


import com.example.demo.dto.analytics.*;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.repository.*;
import com.example.demo.util.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PlayHistoryRepository playHistoryRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private User artist;

    @BeforeEach
    void setup() {
        artist = new User();
        ReflectionTestUtils.setField(artist, "id", 1L);
        artist.setEmail("artist@test.com");
        artist.setName("Artist");
    }

    // ================= ARTIST ANALYTICS =================

    @Test
    void getArtistAnalytics_success() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(artist));

            when(songRepository.countByArtist(artist)).thenReturn(5L);
            when(albumRepository.countByArtist(artist)).thenReturn(2L);
            when(songRepository.sumPlayCountByArtist(artist)).thenReturn(100L);
            when(favoriteRepository.countBySongArtist(artist)).thenReturn(20L);

            Song song1 = new Song();
            song1.setTitle("Song1");
            Song song2 = new Song();
            song2.setTitle("Song2");

            when(songRepository.findTop5ByArtistOrderByPlayCountDesc(artist))
                    .thenReturn(List.of(song1, song2));

            ArtistAnalyticsDTO result = analyticsService.getArtistAnalytics();

            assertNotNull(result);
            assertEquals(5L, result.getTotalSongs());
            assertEquals(2, result.getTopSongs().size());
        }
    }

    @Test
    void getArtistAnalytics_artistNotFound() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("unknown@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> analyticsService.getArtistAnalytics());
        }
    }

    // ================= PLAY CHART =================

    @Test
    void getPlayChart_success() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.of(artist));

            Song song = new Song();
            song.setTitle("My Song");
            song.setPlayCount(50L);

            when(songRepository.findByArtist(artist))
                    .thenReturn(List.of(song));

            List<SongPlayChartDTO> result = analyticsService.getPlayChart();

            assertEquals(1, result.size());
            assertEquals("My Song", result.get(0).getTitle());
        }
    }

    @Test
    void getPlayChart_artistNotFound() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("unknown@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> analyticsService.getPlayChart());
        }
    }

    // ================= LISTENER INSIGHTS =================

    @Test
    void getListenerInsights_success() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.of(artist));

            when(playHistoryRepository.countUniqueListeners(artist))
                    .thenReturn(25L);

            ListenerInsightsDTO result = analyticsService.getListenerInsights();

            assertEquals(25L, result.getUniqueListeners());
        }
    }

    // ================= DAILY TRENDS =================

    @Test
    void getDailyTrends_success() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.of(artist));

            List<Object[]> mockData = new ArrayList<>();
            mockData.add(new Object[]{LocalDateTime.now(), 10L});
            mockData.add(new Object[]{LocalDateTime.now().minusDays(1), 5L});

            when(playHistoryRepository.getDailyPlayCounts(1L))
                    .thenReturn(mockData);

            List<DailyTrendDTO> result = analyticsService.getDailyTrends();

            assertEquals(2, result.size());
        }
    }

    @Test
    void getDailyTrends_artistNotFound() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("unknown@test.com");

            when(userRepository.findByEmail(any()))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> analyticsService.getDailyTrends());
        }
    }
}