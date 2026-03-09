package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.*;
import com.example.demo.service.impl.PlaylistServiceImpl;
import com.example.demo.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaylistServiceImplTest {

    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private PlaylistSongRepository playlistSongRepository;
    private SongRepository songRepository;
    private FollowedPlaylistRepository followedPlaylistRepository;

    private PlaylistServiceImpl playlistService;

    @BeforeEach
    void setup() {
        playlistRepository = mock(PlaylistRepository.class);
        userRepository = mock(UserRepository.class);
        playlistSongRepository = mock(PlaylistSongRepository.class);
        songRepository = mock(SongRepository.class);
        followedPlaylistRepository = mock(FollowedPlaylistRepository.class);

        playlistService = new PlaylistServiceImpl(
                playlistRepository,
                userRepository,
                playlistSongRepository,
                songRepository,
                followedPlaylistRepository
        );
    }

    @Test
    void followPlaylist_Success() {
        User user = new User();
        setId(user, 1L);

        User owner = new User();
        setId(owner, 2L);

        Playlist playlist = new Playlist();
        playlist.setPublic(true);
        playlist.setUser(owner);
        playlist.setName("Test Playlist");
        setId(playlist, 10L);

        // Mock static SecurityUtil
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
            when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));

            playlistService.followPlaylist(10L);

            verify(followedPlaylistRepository).save(any(FollowedPlaylist.class));
        }
    }

    @Test
    void followOwnPlaylist_ShouldThrowException() {
        User user = new User();
        setId(user, 1L);

        Playlist playlist = new Playlist();
        playlist.setPublic(true);
        playlist.setUser(user); // same user
        setId(playlist, 10L);

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("test@mail.com");

            when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
            when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));

            assertThrows(BadRequestException.class, () -> playlistService.followPlaylist(10L));
        }
    }

    // Helper to set private ID field via reflection
    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}