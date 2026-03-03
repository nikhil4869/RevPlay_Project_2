package com.example.demo.serviceImplTest;

import com.example.demo.dto.analytics.SongFavoriteStatsDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.impl.SongServiceImpl;
import com.example.demo.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private SongServiceImpl songService;

    private User mockUser;
    private Song mockSong;
    private Album mockAlbum;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User
        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getName()).thenReturn("Test Artist");

        // Mock Song
        mockSong = mock(Song.class);
        when(mockSong.getId()).thenReturn(10L);
        when(mockSong.getTitle()).thenReturn("Test Song");
        when(mockSong.getGenre()).thenReturn("Pop");
        when(mockSong.getDuration()).thenReturn("03:30");
        when(mockSong.getAudioPath()).thenReturn("/audio/test.mp3");
        when(mockSong.getCoverImage()).thenReturn("/cover/test.jpg");
        when(mockSong.getArtist()).thenReturn(mockUser);
        when(mockSong.getReleaseYear()).thenReturn(2023);

        // Mock Album
        mockAlbum = mock(Album.class);
        when(mockAlbum.getId()).thenReturn(100L);
        when(mockAlbum.getName()).thenReturn("Test Album");
        when(mockAlbum.getArtist()).thenReturn(mockUser);
    }

    // =================== uploadSong ===================
    @Test
    void uploadSong_Success() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("artist@example.com");

            when(userRepository.findByEmail("artist@example.com")).thenReturn(Optional.of(mockUser));
            when(songRepository.existsByTitleIgnoreCaseAndArtist("Test Song", mockUser)).thenReturn(false);
            when(fileStorageService.storeAudio(mockFile)).thenReturn("/audio/test.mp3");
            when(songRepository.save(any(Song.class))).thenReturn(mockSong);

            SongDTO result = songService.uploadSong("Test Song", "Pop", "03:30", mockFile, null, null, 2023);

            assertNotNull(result);
            assertEquals("Test Song", result.getTitle());
            assertEquals("Test Artist", result.getArtistName());
            verify(songRepository, times(1)).save(any(Song.class));
        }
    }

    // =================== getMySongs ===================
    @Test
    void getMySongs_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("artist@example.com");

            when(userRepository.findByEmail("artist@example.com")).thenReturn(Optional.of(mockUser));
            when(songRepository.findByArtist(mockUser)).thenReturn(List.of(mockSong));

            List<SongDTO> songs = songService.getMySongs();
            assertEquals(1, songs.size());
            assertEquals("Test Song", songs.get(0).getTitle());
        }
    }

    // =================== uploadCover ===================
    @Test
    void uploadCover_Success() throws Exception {
        MultipartFile mockImage = mock(MultipartFile.class);

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("artist@example.com");

            when(songRepository.findById(10L)).thenReturn(Optional.of(mockSong));
            when(userRepository.findByEmail("artist@example.com")).thenReturn(Optional.of(mockUser));
            when(fileStorageService.storeImage(mockImage)).thenReturn("/cover/test.jpg");
            when(songRepository.save(any(Song.class))).thenReturn(mockSong);

            SongDTO dto = songService.uploadCover(10L, mockImage);
            assertEquals("Test Artist", dto.getArtistName());
        }
    }

    // =================== getFavoriteStatsForMySongs ===================
    @Test
    void getFavoriteStatsForMySongs_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("artist@example.com");

            when(userRepository.findByEmail("artist@example.com")).thenReturn(Optional.of(mockUser));
            when(songRepository.findByArtist(mockUser)).thenReturn(List.of(mockSong));
            when(favoriteRepository.countBySong(mockSong)).thenReturn(5L);

            List<SongFavoriteStatsDTO> stats = songService.getFavoriteStatsForMySongs();
            assertEquals(1, stats.size());
            assertEquals(5L, stats.get(0).getFavoriteCount());
        }
    }

    // =================== getAlbumSongs ===================
    @Test
    void getAlbumSongs_Success() {
        when(albumRepository.findById(100L)).thenReturn(Optional.of(mockAlbum));
        when(songRepository.findByAlbumOrderByTrackNumberAsc(mockAlbum)).thenReturn(List.of(mockSong));

        List<SongDTO> songs = songService.getAlbumSongs(100L);
        assertEquals(1, songs.size());
        assertEquals("Test Song", songs.get(0).getTitle());
    }

    // =================== deleteSong ===================
    @Test
    void deleteSong_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("artist@example.com");

            when(songRepository.findById(10L)).thenReturn(Optional.of(mockSong));
            when(userRepository.findByEmail("artist@example.com")).thenReturn(Optional.of(mockUser));

            songService.deleteSong(10L);

            verify(fileStorageService).deleteFile("/audio/test.mp3");
            verify(fileStorageService).deleteFile("/cover/test.jpg");
            verify(songRepository).delete(mockSong);
        }
    }

    // =================== getPublicSongs ===================
    @Test
    void getPublicSongs_Success() {
        when(songRepository.findByIsPublicTrue()).thenReturn(List.of(mockSong));

        List<SongDTO> result = songService.getPublicSongs();
        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
    }

    // =================== getAllSongs ===================
    @Test
    void getAllSongs_Success() {
        when(songRepository.findAll()).thenReturn(List.of(mockSong));

        List<SongDTO> result = songService.getAllSongs();
        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
    }

    // =================== getSongDetails ===================
    @Test
    void getSongDetails_Success() {
        when(songRepository.findById(10L)).thenReturn(Optional.of(mockSong));

        SongDTO dto = songService.getSongDetails(10L);
        assertEquals("Test Song", dto.getTitle());
        assertEquals("Test Artist", dto.getArtistName());
    }
}