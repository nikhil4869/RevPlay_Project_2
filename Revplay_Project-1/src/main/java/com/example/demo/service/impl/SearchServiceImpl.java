package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Song;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SongRepository;
import com.example.demo.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final SongRepository songRepository;

    public SearchServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<SongDTO> searchByTitle(String title) {

        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return songRepository
                .findByTitleContainingIgnoreCaseAndIsPublicTrue(title.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDTO> searchByArtist(String artistName) {

        if (artistName == null || artistName.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return songRepository
                .findByArtist_NameContainingIgnoreCaseAndIsPublicTrue(artistName.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDTO> searchByGenre(String genre) {

        if (genre == null || genre.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return songRepository
                .findByGenreContainingIgnoreCaseAndIsPublicTrue(genre.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SongDTO mapToDTO(Song song) {
        return new SongDTO(
                song.getId(),
                song.getTitle(),
                song.getGenre(),
                song.getDuration(),
                song.getAudioPath(),
                song.getCoverImage(),
                song.getArtist().getName()
        );
    }
    
    @Override
    public List<SongDTO> searchByYear(Integer year) {

        List<Song> songs = songRepository.findByReleaseYear(year);

        if (songs.isEmpty()) {
            throw new ResourceNotFoundException("No songs found for year " + year);
        }

        return songs.stream()
                .map(this::mapToDTO)
                .toList();
    }
    
    @Override
    public List<SongDTO> searchByAlbum(String albumName) {

        List<Song> songs = songRepository
                .findByAlbum_NameIgnoreCase(albumName);

        if (songs.isEmpty()) {
            throw new ResourceNotFoundException("Album not found");
        }

        return songs.stream()
                .map(this::mapToDTO)
                .toList();
    }
}