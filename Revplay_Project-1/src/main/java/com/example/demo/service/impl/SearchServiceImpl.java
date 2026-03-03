package com.example.demo.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Song;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.service.SearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class SearchServiceImpl implements SearchService {
	
	private final SongRepository songRepository;
	private final ArtistRepository artistRepository;
	private final AlbumRepository albumRepository;
	
	private static final Logger logger = LogManager.getLogger(SearchServiceImpl.class);
	
	public SearchServiceImpl(SongRepository songRepository, ArtistRepository artistRepository,
			AlbumRepository albumRepository) {
		this.songRepository = songRepository;
		this.artistRepository = artistRepository;
		this.albumRepository = albumRepository;
	}

	@Override
	public List<SongDTO> searchSongs(String keyword) {

	    logger.debug("Searching songs with keyword: {}", keyword);

	    List<SongDTO> songs = songRepository
	            .findByTitleContainingIgnoreCaseAndIsPublicTrue(keyword)
	            .stream()
	            .map(song -> new SongDTO(
	                    song.getId(),
	                    song.getTitle(),
	                    song.getGenre(),
	                    song.getDuration(),
	                    song.getAudioPath(),
	                    song.getCoverImage(),
	                    song.getArtist().getName()
	            ))
	            .collect(Collectors.toList());

	    logger.info("Found {} songs for keyword '{}'", songs.size(), keyword);

	    return songs;
	}


	@Override
	public List<ArtistDTO> searchArtists(String keyword) {

	    logger.debug("Searching artists with keyword: {}", keyword);

	    List<ArtistDTO> artists = artistRepository
	            .findByArtistNameContainingIgnoreCase(keyword)
	            .stream()
	            .map(artist -> {
	                ArtistDTO dto = new ArtistDTO();
	                dto.setArtistName(artist.getArtistName());
	                dto.setProfileImage(artist.getProfileImage());
	                return dto;
	            })
	            .collect(Collectors.toList());

	    logger.info("Found {} artists for keyword '{}'", artists.size(), keyword);

	    return artists;
	}

	@Override
	public List<AlbumDTO> searchAlbums(String keyword) {

	    logger.debug("Searching albums with keyword: {}", keyword);

	    List<AlbumDTO> albums = albumRepository
	            .findByNameContainingIgnoreCase(keyword)
	            .stream()
	            .map(album -> new AlbumDTO(
	                    album.getId(),
	                    album.getName(),
	                    album.getDescription(),
	                    album.getReleaseDate(),
	                    album.getCoverImage(),
	                    album.getArtist().getName()
	            ))
	            .collect(Collectors.toList());

	    logger.info("Found {} albums for keyword '{}'", albums.size(), keyword);

	    return albums;
	}
	
	@Override
	public List<SongDTO> searchByYear(Integer year) {

	    logger.debug("Searching songs by release year: {}", year);

	    List<Song> songs = songRepository.findByReleaseYear(year);

	    if (songs.isEmpty()) {
	        logger.warn("No songs found for year {}", year);
	        throw new ResourceNotFoundException("No songs found for year " + year);
	    }

	    logger.info("Found {} songs for year {}", songs.size(), year);

	    return songs.stream()
	            .map(song -> new SongDTO(
	                    song.getId(),
	                    song.getTitle(),
	                    song.getGenre(),
	                    song.getDuration(),
	                    song.getAudioPath(),
	                    song.getCoverImage(),
	                    song.getArtist().getName()
	            ))
	            .toList();
	}
	
	@Override
	public List<String> getAllGenres() {

	    logger.debug("Fetching all available genres");

	    List<String> genres = songRepository.findAllGenres();

	    logger.info("Fetched {} genres", genres.size());

	    return genres;
	}
	@Override
	public List<SongDTO> searchSongsByGenre(String genre) {

	    logger.debug("Searching songs by genre: {}", genre);

	    List<SongDTO> songs = songRepository
	            .findByGenreIgnoreCaseContaining(genre)
	            .stream()
	            .map(song -> new SongDTO(
	                    song.getId(),
	                    song.getTitle(),
	                    song.getGenre(),
	                    song.getDuration(),
	                    song.getAudioPath(),
	                    song.getCoverImage(),
	                    song.getArtist().getName()
	            ))
	            .toList();

	    logger.info("Found {} songs for genre '{}'", songs.size(), genre);

	    return songs;
	}
	

}
