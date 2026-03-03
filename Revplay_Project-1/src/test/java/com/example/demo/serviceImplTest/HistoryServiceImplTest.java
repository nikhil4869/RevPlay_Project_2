package com.example.demo.serviceImplTest;

import com.example.demo.dto.music.HistoryDTO;
import com.example.demo.dto.music.ListeningTimeDTO;
import com.example.demo.dto.music.MostPlayedDTO;
import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.HistoryServiceImpl;
import com.example.demo.util.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private PlayHistoryRepository historyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HistoryServiceImpl historyService;

    private User loggedInUser;
    private User artistUser;
    private Song song;
    private PlayHistory history;

    @BeforeEach
    void setup() {

        // Logged in user
        loggedInUser = new User();
        loggedInUser.setEmail("test@mail.com");
        loggedInUser.setName("Test User");

        // Artist (also User entity)
        artistUser = new User();
        artistUser.setName("Artist Name");
        artistUser.setEmail("artist@mail.com");

        // Song
        song = new Song();
        song.setTitle("Test Song");
        song.setAudioPath("audio.mp3");
        song.setArtist(artistUser);

        // PlayHistory
        history = new PlayHistory();
        history.setSong(song);
        history.setPlayedAt(LocalDateTime.now());
        history.setDurationPlayed(120);
    }

    @Test
    void getRecentHistory_Success() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(loggedInUser));

            when(historyRepository.findTop50ByUserOrderByPlayedAtDesc(loggedInUser))
                    .thenReturn(List.of(history));

            List<HistoryDTO> result = historyService.getRecentHistory();

            assertEquals(1, result.size());
            assertEquals("Test Song", result.get(0).getTitle());
            assertEquals("Artist Name", result.get(0).getArtistName());
        }
    }

    @Test
    void getFullHistory_Success() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(loggedInUser));

            when(historyRepository.findByUserOrderByPlayedAtDesc(loggedInUser))
                    .thenReturn(List.of(history));

            List<HistoryDTO> result = historyService.getFullHistory();

            assertEquals(1, result.size());
        }
    }

    @Test
    void getListeningTime_Success() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(loggedInUser));

            when(historyRepository.findByUserOrderByPlayedAtDesc(loggedInUser))
                    .thenReturn(List.of(history));

            ListeningTimeDTO result = historyService.getListeningTime();

            assertEquals(120, result.getSeconds());
        }
    }

    @Test
    void getMostPlayed_Success() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(loggedInUser));

            List<Object[]> mockResult = new java.util.ArrayList<>();
            mockResult.add(new Object[]{1L, "Test Song", 5L});

            when(historyRepository.findMostPlayedSongs(loggedInUser))
                .thenReturn(mockResult);

            List<MostPlayedDTO> result = historyService.getMostPlayed();

            assertEquals(1, result.size());
            assertEquals("Test Song", result.get(0).getTitle());
            assertEquals(5L, result.get(0).getPlays());
        }
    }

    @Test
    void clearHistory_Success() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(loggedInUser));

            historyService.clearHistory();

            verify(historyRepository).deleteByUser(loggedInUser);
        }
    }

    @Test
    void userNotFound_ShouldThrowException() {

        try (MockedStatic<SecurityUtil> mockedStatic =
                     mockStatic(SecurityUtil.class)) {

            mockedStatic.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> historyService.getRecentHistory());
        }
    }
}