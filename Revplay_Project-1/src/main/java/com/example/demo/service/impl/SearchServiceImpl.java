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

    private static final Logger logger = LogManager.getLogger(SearchServiceImpl.class);

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    public SearchServiceImpl(SongRepository songRepository,
                             ArtistRepository artistRepository,
                             AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;

        logger.info("SearchServiceImpl initialized");
    }

    @Override
    public List<SongDTO> searchSongs(String keyword) {

        logger.info("Searching songs with keyword={}", keyword);

        List<SongDTO> songs = songRepository
                .searchPublicSongs(keyword)
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName(),
                        song.getAlbum() != null ? song.getAlbum().getName() : null
                ))
                .collect(Collectors.toList());

        logger.info("Songs found count={}", songs.size());

        return songs;
    }

    @Override
    public List<ArtistDTO> searchArtists(String keyword) {

        logger.info("Searching artists with keyword={}", keyword);

        List<ArtistDTO> artists = artistRepository
                .findByArtistNameContainingIgnoreCase(keyword)
                .stream()
                .map(artist -> {
                    ArtistDTO dto = new ArtistDTO();
                    dto.setId(artist.getId());
                    dto.setArtistName(artist.getArtistName());
                    dto.setProfileImage(artist.getProfileImage());
                    return dto;
                })
                .collect(Collectors.toList());

        logger.info("Artists found count={}", artists.size());

        return artists;
    }

    @Override
    public List<AlbumDTO> searchAlbums(String keyword) {

        logger.info("Searching albums with keyword={}", keyword);

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

        logger.info("Albums found count={}", albums.size());

        return albums;
    }

    @Override
    public List<SongDTO> searchByYear(Integer year) {

        logger.info("Searching songs by year={}", year);

        List<SongDTO> songs = songRepository
                .findByReleaseYearAndIsPublicTrue(year)
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName(),
                        song.getAlbum() != null ? song.getAlbum().getName() : null
                ))
                .toList();

        logger.info("Songs found for year {} count={}", year, songs.size());

        return songs;
    }

    @Override
    public List<String> getAllGenres() {

        logger.info("Fetching all genres");

        List<String> genres = songRepository.findAllGenres();

        logger.info("Genres found count={}", genres.size());

        return genres;
    }

    @Override
    public List<Integer> getAllYears() {

        logger.info("Fetching all release years");

        List<Integer> years = songRepository.findAllReleaseYears();

        logger.info("Years found count={}", years.size());

        return years;
    }

    @Override
    public List<ArtistDTO> getAllArtists() {

        logger.info("Fetching all artists");

        List<ArtistDTO> artists = artistRepository.findAll().stream()
                .map(artist -> {
                    ArtistDTO dto = new ArtistDTO();
                    dto.setId(artist.getId());
                    dto.setArtistName(artist.getArtistName());
                    dto.setProfileImage(artist.getProfileImage());
                    return dto;
                })
                .collect(Collectors.toList());

        logger.info("Artists fetched count={}", artists.size());

        return artists;
    }

    @Override
    public List<AlbumDTO> getAllAlbums() {

        logger.info("Fetching all albums");

        List<AlbumDTO> albums = albumRepository.findAll().stream()
                .map(album -> new AlbumDTO(
                        album.getId(),
                        album.getName(),
                        album.getDescription(),
                        album.getReleaseDate(),
                        album.getCoverImage(),
                        album.getArtist().getName()
                ))
                .collect(Collectors.toList());

        logger.info("Albums fetched count={}", albums.size());

        return albums;
    }

    @Override
    public List<SongDTO> searchSongsByGenre(String genre) {

        logger.info("Searching songs by genre={}", genre);

        List<SongDTO> songs = songRepository
                .findByGenreIgnoreCaseContainingAndIsPublicTrue(genre)
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName(),
                        song.getAlbum() != null ? song.getAlbum().getName() : null
                ))
                .toList();

        logger.info("Songs found for genre {} count={}", genre, songs.size());

        return songs;
    }

    @Override
    public List<SongDTO> searchSongsByArtist(Long artistId) {

        logger.info("Searching songs by artistId={}", artistId);

        com.example.demo.entity.ArtistProfile profile = artistRepository.findById(artistId).orElse(null);

        if (profile == null) {
            logger.warn("Artist profile not found artistId={}", artistId);
            return java.util.Collections.emptyList();
        }

        Long userId = profile.getUser().getId();

        List<SongDTO> songs = songRepository
                .findByArtist_IdAndIsPublicTrue(userId)
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName(),
                        song.getAlbum() != null ? song.getAlbum().getName() : null
                ))
                .toList();

        logger.info("Songs found for artistId={} count={}", artistId, songs.size());

        return songs;
    }

    @Override
    public List<SongDTO> searchSongsByAlbum(Long albumId) {

        logger.info("Searching songs by albumId={}", albumId);

        List<SongDTO> songs = songRepository
                .findByAlbum_IdAndIsPublicTrue(albumId)
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName(),
                        song.getAlbum() != null ? song.getAlbum().getName() : null
                ))
                .toList();

        logger.info("Songs found for albumId={} count={}", albumId, songs.size());

        return songs;
    }
}