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

@Service
public class SearchServiceImpl implements SearchService {
	
	private final SongRepository songRepository;
	private final ArtistRepository artistRepository;
	private final AlbumRepository albumRepository;
	
	
	
	public SearchServiceImpl(SongRepository songRepository, ArtistRepository artistRepository,
			AlbumRepository albumRepository) {
		this.songRepository = songRepository;
		this.artistRepository = artistRepository;
		this.albumRepository = albumRepository;
	}

	@Override
	public List<SongDTO> searchSongs(String keyword) {

	    return songRepository
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
	}


	@Override
	public List<ArtistDTO> searchArtists(String keyword) {

	    return artistRepository
	            .findByArtistNameContainingIgnoreCase(keyword)
	            .stream()
	            .map(artist -> {
	                ArtistDTO dto = new ArtistDTO();
	                dto.setArtistName(artist.getArtistName());
	                dto.setProfileImage(artist.getProfileImage());
	                return dto;
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public List<AlbumDTO> searchAlbums(String keyword) {

	    return albumRepository
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
	}
	
	@Override
    public List<SongDTO> searchByYear(Integer year) {

        List<Song> songs = songRepository.findByReleaseYear(year);

        if (songs.isEmpty()) {
            throw new ResourceNotFoundException("No songs found for year " + year);
        }

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
	    return songRepository.findAllGenres();
	}

	@Override
	public List<SongDTO> searchSongsByGenre(String genre) {

	    return songRepository
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
	}
	

}
