package com.example.demo.service.impl;

import com.example.demo.entity.Album;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
<<<<<<< HEAD
=======
import com.example.demo.exception.DuplicateResourceException;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AlbumService;
import com.example.demo.service.FileStorageService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.AlbumDTO;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AlbumServiceImpl implements AlbumService {

	private final AlbumRepository albumRepository;
	private final UserRepository userRepository;
	private final FileStorageService fileStorageService;
	private final SongRepository songRepository;

	private static final Logger logger = LogManager.getLogger(AlbumServiceImpl.class);

	public AlbumServiceImpl(AlbumRepository albumRepository, UserRepository userRepository,
			FileStorageService fileStorageService, SongRepository songRepository) {

<<<<<<< HEAD
    @Override
    public AlbumDTO createAlbum(String name,
<<<<<<< HEAD
                             String description,
                             LocalDate releaseDate) {
=======
                                String description,
                                LocalDate releaseDate) {
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
=======
		this.albumRepository = albumRepository;
		this.userRepository = userRepository;
		this.fileStorageService = fileStorageService;
		this.songRepository = songRepository;
	}
>>>>>>> d4f4593 (Initial commit of RevPlay project)

	private AlbumDTO mapToDTO(Album album) {
		return new AlbumDTO(album.getId(), album.getName(), album.getDescription(), album.getReleaseDate(),
				album.getCoverImage(), album.getArtist().getName());
	}

	@Override
	public AlbumDTO createAlbum(String name, String description, LocalDate releaseDate) {

<<<<<<< HEAD
<<<<<<< HEAD
=======
        // 🚫 prevent duplicate albums
        if (albumRepository.existsByNameIgnoreCaseAndArtist(name, artist)) {
            throw new DuplicateResourceException("Album with this name already exists");
        }

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setReleaseDate(releaseDate);
        album.setArtist(artist);
=======
		logger.debug("Attempting to create album with name: {}", name);

		String email = SecurityUtil.getCurrentUserEmail();
		logger.debug("Fetching artist by email: {}", email);
>>>>>>> d4f4593 (Initial commit of RevPlay project)

		User artist = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new ResourceNotFoundException("User not found");
		});

		if (albumRepository.existsByNameIgnoreCaseAndArtist(name, artist)) {
			logger.warn("Duplicate album creation attempt. Album name: {}, Artist id: {}", name, artist.getId());
			throw new DuplicateResourceException("Album with this name already exists");
		}

		Album album = new Album();
		album.setName(name);
		album.setDescription(description);
		album.setReleaseDate(releaseDate);
		album.setArtist(artist);

		Album savedAlbum = albumRepository.save(album);
		logger.info("Album created successfully. Album id: {}, Artist id: {}", savedAlbum.getId(), artist.getId());

		return mapToDTO(savedAlbum);
	}

	@Override
	public AlbumDTO uploadCover(Long albumId, MultipartFile image) {

		logger.debug("Uploading cover for album id: {}", albumId);

		Album album = albumRepository.findById(albumId).orElseThrow(() -> {
			logger.error("Album not found with id: {}", albumId);
			return new ResourceNotFoundException("Album not found");
		});

		String email = SecurityUtil.getCurrentUserEmail();
		User currentUser = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new ResourceNotFoundException("User not found");
		});

		if (!album.getArtist().getId().equals(currentUser.getId())) {
			logger.warn("Unauthorized cover upload attempt. Album id: {}, User id: {}", albumId, currentUser.getId());
			throw new BadRequestException("Unauthorized access");
		}

<<<<<<< HEAD
        return albumRepository.findByArtist(artist)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
<<<<<<< HEAD
=======
    
    @Override
    public AlbumDTO getAlbumDetails(Long albumId) {
=======
		String imagePath = fileStorageService.storeImage(image);
		album.setCoverImage(imagePath);
>>>>>>> d4f4593 (Initial commit of RevPlay project)

		Album updatedAlbum = albumRepository.save(album);
		logger.info("Cover uploaded successfully for album id: {}", albumId);

		return mapToDTO(updatedAlbum);
	}

	@Override
	public List<AlbumDTO> getMyAlbums() {

<<<<<<< HEAD
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
=======
		logger.debug("Fetching albums for current user");

		String email = SecurityUtil.getCurrentUserEmail();

		User artist = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new ResourceNotFoundException("User not found");
		});

		List<AlbumDTO> albums = albumRepository.findByArtist(artist).stream().map(this::mapToDTO)
				.collect(Collectors.toList());

		logger.info("Fetched {} albums for artist id: {}", albums.size(), artist.getId());

		return albums;
	}

	@Override
	public AlbumDTO getAlbumDetails(Long albumId) {

		logger.debug("Fetching album details for id: {}", albumId);

		Album album = albumRepository.findById(albumId).orElseThrow(() -> {
			logger.error("Album not found with id: {}", albumId);
			return new ResourceNotFoundException("Album not found");
		});

		logger.info("Album details retrieved successfully for id: {}", albumId);

		return mapToDTO(album);
	}

	@Override
	public void deleteAlbum(Long albumId) {

		logger.debug("Attempting to delete album with id: {}", albumId);

		Album album = albumRepository.findById(albumId).orElseThrow(() -> {
			logger.error("Album not found with id: {}", albumId);
			return new ResourceNotFoundException("Album not found");
		});

		String email = SecurityUtil.getCurrentUserEmail();
		User currentUser = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new ResourceNotFoundException("User not found");
		});

		if (!album.getArtist().getId().equals(currentUser.getId())) {
			logger.warn("Unauthorized delete attempt. Album id: {}, User id: {}", albumId, currentUser.getId());
			throw new BadRequestException("Unauthorized access");
		}

		List<Song> songs = songRepository.findByAlbum(album);
		logger.debug("Detaching {} songs from album id: {}", songs.size(), albumId);

		for (Song song : songs) {
			song.setAlbum(null);
		}

		songRepository.saveAll(songs);
		albumRepository.delete(album);

		logger.info("Album deleted successfully. Album id: {}", albumId);
	}

	@Override
	public AlbumDTO updateAlbum(Long albumId, String name, String description, LocalDate releaseDate) {

		logger.debug("Updating album id: {}", albumId);

		Album album = albumRepository.findById(albumId).orElseThrow(() -> {
			logger.error("Album not found with id: {}", albumId);
			return new ResourceNotFoundException("Album not found");
		});

		String email = SecurityUtil.getCurrentUserEmail();
		User currentUser = userRepository.findByEmail(email).orElseThrow(() -> {
			logger.error("User not found with email: {}", email);
			return new ResourceNotFoundException("User not found");
		});

		if (!album.getArtist().getId().equals(currentUser.getId())) {
			logger.warn("Unauthorized update attempt. Album id: {}, User id: {}", albumId, currentUser.getId());
			throw new BadRequestException("Unauthorized access");
		}

		album.setName(name);
		album.setDescription(description);
		album.setReleaseDate(releaseDate);

		Album updatedAlbum = albumRepository.save(album);

		logger.info("Album updated successfully. Album id: {}", albumId);

		return mapToDTO(updatedAlbum);
	}

	@Override
	public List<AlbumDTO> getAllAlbums() {

		logger.debug("Fetching all albums");

		List<AlbumDTO> albums = albumRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());

		logger.info("Total albums fetched: {}", albums.size());

		return albums;
	}
>>>>>>> d4f4593 (Initial commit of RevPlay project)

}