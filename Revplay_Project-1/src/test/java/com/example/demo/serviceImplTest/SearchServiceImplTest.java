package com.example.demo.serviceImplTest;
import com.example.demo.service.impl.SearchServiceImpl;
import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.ArtistProfile;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    private Song song;
    private Album album;
    private ArtistProfile artistProfile;
    private User artistUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Artist User for foreign key in Album
        artistUser = new User();

        // ArtistProfile for searchArtists()
        artistProfile = new ArtistProfile();
        artistProfile.setArtistName("Test Artist");
        artistProfile.setProfileImage("/images/artist.jpg");
        artistProfile.setUser(artistUser);

        // Song
        song = new Song();
        song.setTitle("Test Song");
        song.setGenre("Pop");
        song.setDuration("03:30");
        song.setAudioPath("/music/test.mp3");
        song.setCoverImage("/images/song.jpg");
        song.setReleaseYear(2023);
        song.setArtist(artistUser); // Assuming Song has getArtist() -> User

        // Album
        album = new Album();
        album.setName("Test Album");
        album.setDescription("Album Description");
        album.setReleaseDate(LocalDate.of(2023, 1, 1));
        album.setCoverImage("/images/album.jpg");
        album.setArtist(artistUser);
    }

    // ===================== searchSongs =====================
    @Test
    void searchSongs_ShouldReturnSongDTOs() {
        when(songRepository.findByTitleContainingIgnoreCaseAndIsPublicTrue("Test"))
                .thenReturn(List.of(song));

        List<SongDTO> result = searchService.searchSongs("Test");

        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        verify(songRepository, times(1))
                .findByTitleContainingIgnoreCaseAndIsPublicTrue("Test");
    }

    // ===================== searchArtists =====================
    @Test
    void searchArtists_ShouldReturnArtistDTOs() {
        when(artistRepository.findByArtistNameContainingIgnoreCase("Test"))
                .thenReturn(List.of(artistProfile));

        List<ArtistDTO> result = searchService.searchArtists("Test");

        assertEquals(1, result.size());
        assertEquals("Test Artist", result.get(0).getArtistName());
        verify(artistRepository, times(1)).findByArtistNameContainingIgnoreCase("Test");
    }

    // ===================== searchAlbums =====================
    @Test
    void searchAlbums_ShouldReturnAlbumDTOs() {
        // Create a mock User for album artist
        User albumArtist = mock(User.class);
        when(albumArtist.getName()).thenReturn("Test Artist"); // mock the getter

        // Create Album with mocked artist
        Album album = new Album();
        album.setName("Test Album");
        album.setDescription("Album Description");
        album.setReleaseDate(LocalDate.of(2023, 1, 1));
        album.setCoverImage("/images/album.jpg");
        album.setArtist(albumArtist);

        // Mock repository
        when(albumRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(List.of(album));

        // Call the service method
        List<AlbumDTO> result = searchService.searchAlbums("Test");

        // Assertions
        assertEquals(1, result.size());
        assertEquals("Test Album", result.get(0).getName());
        assertEquals("Test Artist", result.get(0).getArtistName());

        // Verify repository interaction
        verify(albumRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }
    // ===================== searchByYear =====================
    @Test
    void searchByYear_WhenSongsExist_ShouldReturnSongDTOs() {
        when(songRepository.findByReleaseYear(2023)).thenReturn(List.of(song));

        List<SongDTO> result = searchService.searchByYear(2023);

        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        verify(songRepository, times(1)).findByReleaseYear(2023);
    }

    @Test
    void searchByYear_WhenNoSongsExist_ShouldThrowException() {
        when(songRepository.findByReleaseYear(2022)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> searchService.searchByYear(2022));
        verify(songRepository, times(1)).findByReleaseYear(2022);
    }

    // ===================== getAllGenres =====================
    @Test
    void getAllGenres_ShouldReturnListOfGenres() {
        List<String> genres = List.of("Pop", "Rock");
        when(songRepository.findAllGenres()).thenReturn(genres);

        List<String> result = searchService.getAllGenres();

        assertEquals(2, result.size());
        assertTrue(result.contains("Pop"));
        assertTrue(result.contains("Rock"));
        verify(songRepository, times(1)).findAllGenres();
    }

    // ===================== searchSongsByGenre =====================
    @Test
    void searchSongsByGenre_ShouldReturnSongDTOs() {
        when(songRepository.findByGenreIgnoreCaseContaining("Pop")).thenReturn(List.of(song));

        List<SongDTO> result = searchService.searchSongsByGenre("Pop");

        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getTitle());
        verify(songRepository, times(1)).findByGenreIgnoreCaseContaining("Pop");
    }
}