package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.FavoriteServiceImpl;
import com.example.demo.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private Song song;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        song = new Song();
        song.setTitle("Test Song");
        song.setArtist(user);
        song.setAudioPath("audio.mp3"); // needed for mapToDTO
    }

    // -------------------- ADD FAVORITE --------------------
    @Test
    void addFavorite_ShouldAddSuccessfully() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(songRepository.findById(1L)).thenReturn(Optional.of(song));
            when(favoriteRepository.findByUserAndSong(user, song)).thenReturn(Optional.empty());

            favoriteService.addFavorite(1L);

            verify(favoriteRepository).save(any(Favorite.class));
        }
    }

    @Test
    void addFavorite_ShouldThrowIfUserNotFound() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                favoriteService.addFavorite(1L);
            });
            assertEquals("User not found", ex.getMessage());
        }
    }

    @Test
    void addFavorite_ShouldThrowIfSongNotFound() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(songRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                favoriteService.addFavorite(1L);
            });
            assertEquals("Song not found", ex.getMessage());
        }
    }

    @Test
    void addFavorite_ShouldThrowIfAlreadyFavorited() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setSong(song);

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(songRepository.findById(1L)).thenReturn(Optional.of(song));
            when(favoriteRepository.findByUserAndSong(user, song)).thenReturn(Optional.of(fav));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> {
                favoriteService.addFavorite(1L);
            });
            assertEquals("Song already in favorites", ex.getMessage());
        }
    }

    // -------------------- REMOVE FAVORITE --------------------
    @Test
    void removeFavorite_ShouldRemoveSuccessfully() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setSong(song);

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(songRepository.findById(1L)).thenReturn(Optional.of(song));
            when(favoriteRepository.findByUserAndSong(user, song)).thenReturn(Optional.of(fav));

            favoriteService.removeFavorite(1L);

            verify(favoriteRepository).delete(fav);
        }
    }

    @Test
    void removeFavorite_ShouldThrowIfFavoriteNotFound() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(songRepository.findById(1L)).thenReturn(Optional.of(song));
            when(favoriteRepository.findByUserAndSong(user, song)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                favoriteService.removeFavorite(1L);
            });
            assertEquals("Favorite not found", ex.getMessage());
        }
    }

    // -------------------- GET MY FAVORITES --------------------
    @Test
    void getMyFavorites_ShouldReturnFavorites() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setSong(song);

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(favoriteRepository.findByUser(user)).thenReturn(Collections.singletonList(fav));

            List<SongDTO> result = favoriteService.getMyFavorites();

            assertEquals(1, result.size());
            assertEquals("Test Song", result.get(0).getTitle());
        }
    }

    @Test
    void getMyFavorites_ShouldThrowIfUserNotFound() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                favoriteService.getMyFavorites();
            });
            assertEquals("User not found", ex.getMessage());
        }
    }
}