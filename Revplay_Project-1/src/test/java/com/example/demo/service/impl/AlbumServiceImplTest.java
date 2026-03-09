package com.example.demo.service.impl;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.util.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

	@Mock
	private AlbumRepository albumRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FileStorageService fileStorageService;

	@Mock
	private SongRepository songRepository;

	@InjectMocks
	private AlbumServiceImpl albumService;

	private User user;
	private Album album;

	@BeforeEach
	void setup() {
		user = new User();
		ReflectionTestUtils.setField(user, "id", 1L);
		user.setEmail("artist@test.com");
		user.setName("Artist");

		album = new Album();
		ReflectionTestUtils.setField(album, "id", 10L);
		album.setName("Test Album");
		album.setDescription("Test Description");
		album.setReleaseDate(LocalDate.now());
		album.setArtist(user);
	}

	

	@Test
	void createAlbum_success() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

			when(albumRepository.existsByNameIgnoreCaseAndArtist("Test Album", user)).thenReturn(false);

			when(albumRepository.save(any(Album.class))).thenReturn(album);

			AlbumDTO result = albumService.createAlbum("Test Album", "Test Description", LocalDate.now());

			assertNotNull(result);
			assertEquals("Test Album", result.getName());
			verify(albumRepository).save(any(Album.class));
		}
	}

	@Test
	void createAlbum_userNotFound() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("unknown@test.com");

			when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class,
					() -> albumService.createAlbum("Test", "Desc", LocalDate.now()));
		}
	}

	@Test
	void createAlbum_duplicate() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

			when(albumRepository.existsByNameIgnoreCaseAndArtist(any(), any())).thenReturn(true);

			assertThrows(DuplicateResourceException.class,
					() -> albumService.createAlbum("Test", "Desc", LocalDate.now()));

			verify(albumRepository, never()).save(any());
		}
	}

	

	@Test
	void uploadCover_success() {
		MultipartFile file = mock(MultipartFile.class);

		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

			when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

			when(fileStorageService.storeImage(file)).thenReturn("image/path.jpg");

			when(albumRepository.save(album)).thenReturn(album);

			AlbumDTO result = albumService.uploadCover(10L, file);

			assertNotNull(result);
			verify(fileStorageService).storeImage(file);
			verify(albumRepository).save(album);
		}
	}

	@Test
	void uploadCover_notFound() {
		when(albumRepository.findById(10L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> albumService.uploadCover(10L, mock(MultipartFile.class)));
	}

	@Test
	void uploadCover_unauthorized() {
		User otherUser = new User();
		ReflectionTestUtils.setField(otherUser, "id", 99L);

		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@test.com");

			when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

			when(userRepository.findByEmail(any())).thenReturn(Optional.of(otherUser));

			assertThrows(BadRequestException.class, () -> albumService.uploadCover(10L, mock(MultipartFile.class)));
		}
	}

	

	@Test
	void getMyAlbums_success() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

			when(albumRepository.findByArtist(user)).thenReturn(List.of(album));

			List<AlbumDTO> result = albumService.getMyAlbums();

			assertEquals(1, result.size());
		}
	}

	// ================= GET DETAILS =================

	@Test
	void getAlbumDetails_success() {
		when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

		AlbumDTO result = albumService.getAlbumDetails(10L);

		assertEquals("Test Album", result.getName());
	}

	@Test
	void getAlbumDetails_notFound() {
		when(albumRepository.findById(10L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> albumService.getAlbumDetails(10L));
	}

	// ================= DELETE =================

	@Test
	void deleteAlbum_success() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

			when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

			when(songRepository.findByAlbum(album)).thenReturn(new ArrayList<>());

			albumService.deleteAlbum(10L);

			verify(songRepository).saveAll(anyList());
			verify(albumRepository).delete(album);
		}
	}

	@Test
	void deleteAlbum_unauthorized() {
		User otherUser = new User();
		ReflectionTestUtils.setField(otherUser, "id", 55L);

		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@test.com");

			when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

			when(userRepository.findByEmail(any())).thenReturn(Optional.of(otherUser));

			assertThrows(BadRequestException.class, () -> albumService.deleteAlbum(10L));
		}
	}

	// ================= UPDATE =================

	@Test
	void updateAlbum_success() {
		try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

			mocked.when(SecurityUtil::getCurrentUserEmail).thenReturn(user.getEmail());

			when(albumRepository.findById(10L)).thenReturn(Optional.of(album));

			when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

			when(albumRepository.save(any())).thenReturn(album);

			AlbumDTO result = albumService.updateAlbum(10L, "Updated", "Updated Desc", LocalDate.now());

			assertNotNull(result);
			verify(albumRepository).save(any());
		}
	}

	// ================= GET ALL =================

	@Test
	void getAllAlbums_success() {
		when(albumRepository.findAll()).thenReturn(List.of(album));

		List<AlbumDTO> result = albumService.getAllAlbums();

		assertEquals(1, result.size());
	}
}