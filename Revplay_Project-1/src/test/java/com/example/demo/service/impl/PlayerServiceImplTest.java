package com.example.demo.service.impl;


import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.Role;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.PlayerServiceImpl;
import com.example.demo.util.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayHistoryRepository historyRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private User user;
    private Role role;
    private Song song;

    @BeforeEach
    void setup() {

        role = new Role();
        role.setName("user");

        user = new User();
        user.setEmail("test@mail.com");
        user.setName("Test User");
        user.setRole(role);

        song = new Song();
        song.setTitle("Test Song");
        song.setDuration("2:00");
        song.setPlayCount(0L);
        song.setArtist(user);
    }

    @Test
    void playSong_UserRole_ShouldIncrementPlayCountAndSaveHistory() {

        try (MockedStatic<SecurityUtil> mocked =
                     mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(songRepository.findById(1L))
                    .thenReturn(Optional.of(song));

            playerService.playSong(1L);

            assertEquals(1L, song.getPlayCount());

            verify(songRepository).save(song);
            verify(historyRepository).save(any(PlayHistory.class));
        }
    }

    @Test
    void playSong_AdminRole_ShouldNotIncrementPlayCount() {

        role.setName("admin");   // change role

        try (MockedStatic<SecurityUtil> mocked =
                     mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(songRepository.findById(1L))
                    .thenReturn(Optional.of(song));

            playerService.playSong(1L);

            assertEquals(0L, song.getPlayCount()); // no increment

            verify(songRepository, never()).save(song);
            verify(historyRepository).save(any(PlayHistory.class));
        }
    }

    @Test
    void playSong_UserNotFound_ShouldThrowException() {

        try (MockedStatic<SecurityUtil> mocked =
                     mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> playerService.playSong(1L));
        }
    }

    @Test
    void playSong_SongNotFound_ShouldThrowException() {

        try (MockedStatic<SecurityUtil> mocked =
                     mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(songRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> playerService.playSong(1L));
        }
    }

    @Test
    void getTrendingSongs_Success() {

        when(songRepository.findByIsPublicTrueOrderByPlayCountDesc())
                .thenReturn(List.of(song));

        List<SongDTO> result = playerService.getTrendingSongs(1);

        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        assertEquals("Test User", result.get(0).getArtistName());
    }
}