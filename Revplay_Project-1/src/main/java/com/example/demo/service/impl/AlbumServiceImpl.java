package com.example.demo.service.impl;

import com.example.demo.entity.Album;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
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

	private static final Logger logger = LogManager.getLogger(AlbumServiceImpl.class);

	private final AlbumRepository albumRepository;
	private final UserRepository userRepository;
	private final FileStorageService fileStorageService;
	private final SongRepository songRepository;

	public AlbumServiceImpl(AlbumRepository albumRepository, UserRepository userRepository,
			FileStorageService fileStorageService, SongRepository songRepository) {

		this.albumRepository = albumRepository;
		this.userRepository = userRepository;
		this.fileStorageService = fileStorageService;
		this.songRepository = songRepository;

		logger.info("AlbumServiceImpl initialized");
	}

	private AlbumDTO mapToDTO(Album album) {

		logger.debug("Mapping Album to DTO. AlbumId={}", album.getId());

		return new AlbumDTO(album.getId(), album.getName(), album.getDescription(), album.getReleaseDate(),
				album.getCoverImage(), album.getArtist().getName());
	}

	@Override
	public AlbumDTO createAlbum(String name, String description, LocalDate releaseDate) {

		logger.info("Creating album with name={}", name);

		String email = SecurityUtil.getCurrentUserEmail();

		logger.debug("Current user email={}", email);

		User artist = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// 🚫 prevent duplicate albums
		if (albumRepository.existsByNameIgnoreCaseAndArtist(name, artist)) {

			logger.warn("Duplicate album attempt detected for artistId={} albumName={}", artist.getId(), name);

			throw new DuplicateResourceException("Album with this name already exists");
		}

		Album album = new Album();
		album.setName(name);
		album.setDescription(description);
		album.setReleaseDate(releaseDate);
		album.setArtist(artist);

		logger.info("Album created successfully for artistId={}", artist.getId());

		return mapToDTO(albumRepository.save(album));
	}

	@Override
	public AlbumDTO uploadCover(Long albumId, MultipartFile image) {

		logger.info("Uploading cover image for albumId={}", albumId);

		Album album = albumRepository.findById(albumId)
				.orElseThrow(() -> new ResourceNotFoundException("Album not found"));

		String email = SecurityUtil.getCurrentUserEmail();

		User currentUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!album.getArtist().getId().equals(currentUser.getId())) {

			logger.warn("Unauthorized cover upload attempt by userId={} for albumId={}", currentUser.getId(), albumId);

			throw new BadRequestException("Unauthorized access");
		}

		String imagePath = fileStorageService.storeImage(image);

		logger.debug("Image stored at path={}", imagePath);

		album.setCoverImage(imagePath);

		logger.info("Cover image uploaded successfully for albumId={}", albumId);

		return mapToDTO(albumRepository.save(album));
	}

	@Override
	public List<AlbumDTO> getMyAlbums() {

		logger.info("Fetching albums for current user");

		String email = SecurityUtil.getCurrentUserEmail();

		User artist = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<AlbumDTO> albums = albumRepository.findByArtist(artist).stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());

		logger.info("Found {} albums for artistId={}", albums.size(), artist.getId());

		return albums;
	}

	@Override
	public AlbumDTO getAlbumDetails(Long albumId) {

		logger.info("Fetching album details for albumId={}", albumId);

		Album album = albumRepository.findById(albumId)
				.orElseThrow(() -> new ResourceNotFoundException("Album not found"));

		return mapToDTO(album);
	}

	@Override
	public void deleteAlbum(Long albumId) {

	    logger.info("Deleting album with albumId={}", albumId);

	    Album album = albumRepository.findById(albumId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Album not found"));

	    String email = SecurityUtil.getCurrentUserEmail();

	    User currentUser = userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("User not found"));

	    if (!album.getArtist().getId()
	            .equals(currentUser.getId())) {

	        logger.warn("Unauthorized delete attempt by userId={} for albumId={}", currentUser.getId(), albumId);

	        throw new BadRequestException("Unauthorized access");
	    }

	    // 🔥 Detach songs from album
	    List<Song> songs = songRepository.findByAlbum(album);

	    logger.debug("Detaching {} songs from albumId={}", songs.size(), albumId);

	    for (Song song : songs) {
	        song.setAlbum(null);
	    }

	    songRepository.saveAll(songs);

	    // 🔥 Now delete album safely
	    albumRepository.delete(album);

	    logger.info("Album deleted successfully. albumId={}", albumId);
	}

	@Override
	public AlbumDTO updateAlbum(Long albumId, String name, String description, LocalDate releaseDate) {

		logger.info("Updating album with albumId={}", albumId);

		Album album = albumRepository.findById(albumId)
				.orElseThrow(() -> new ResourceNotFoundException("Album not found"));

		String email = SecurityUtil.getCurrentUserEmail();

		User currentUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!album.getArtist().getId().equals(currentUser.getId())) {

			logger.warn("Unauthorized update attempt by userId={} for albumId={}", currentUser.getId(), albumId);

			throw new BadRequestException("Unauthorized access");
		}

		album.setName(name);
		album.setDescription(description);
		album.setReleaseDate(releaseDate);

		logger.info("Album updated successfully. albumId={}", albumId);

		return mapToDTO(albumRepository.save(album));
	}

	@Override
	public List<AlbumDTO> getAllAlbums() {

		logger.info("Fetching all albums");

		List<AlbumDTO> albums = albumRepository.findAll().stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());

		logger.info("Total albums found={}", albums.size());

		return albums;
	}
}