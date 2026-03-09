package com.example.demo.service.impl;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.SongService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.SongDTO;
import java.util.List;
import com.example.demo.entity.Album;
import com.example.demo.repository.AlbumRepository;
import java.util.stream.Collectors;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.dto.analytics.SongFavoriteStatsDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SongServiceImpl implements SongService {

    private static final Logger logger = LogManager.getLogger(SongServiceImpl.class);

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AlbumRepository albumRepository;
    private final FavoriteRepository favoriteRepository;

    public SongServiceImpl(SongRepository songRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            AlbumRepository albumRepository,
            FavoriteRepository favoriteRepository) {
         this.songRepository = songRepository;
         this.userRepository = userRepository;
         this.fileStorageService = fileStorageService;
         this.albumRepository = albumRepository;
         this.favoriteRepository = favoriteRepository;

         logger.info("SongServiceImpl initialized");
    }

    @Override
    public SongDTO uploadSong(String title,
            String genre,
            String duration,
            MultipartFile audioFile,
            Long albumId,
            Integer trackNumber,
            Integer releaseYear) {

        logger.info("Uploading new song title={}", title);

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current artist email={}", email);

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (songRepository.existsByTitleIgnoreCaseAndArtist(title, artist)) {
            logger.warn("Duplicate song upload attempt title={} artistId={}", title, artist.getId());
            throw new DuplicateResourceException("Song already exists");
        }

        String filePath = fileStorageService.storeAudio(audioFile);

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre.trim().toLowerCase());
        song.setDuration(duration);
        song.setAudioPath(filePath);
        song.setArtist(artist);
        song.setReleaseYear(releaseYear);

        if (albumId != null) {

            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

            song.setAlbum(album);
            song.setTrackNumber(trackNumber);
        }

        Song saved = songRepository.save(song);

        logger.info("Song uploaded successfully songId={}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    public List<SongDTO> getMySongs() {

        logger.info("Fetching songs for current artist");

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SongDTO> songs = songRepository.findByArtist(artist)
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Songs fetched count={}", songs.size());

        return songs;
    }

    @Override
    public SongDTO uploadCover(Long songId, MultipartFile image) {

        logger.info("Uploading cover image for songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized cover upload attempt songId={}", songId);
            throw new BadRequestException("Unauthorized access");
        }

        String imagePath = fileStorageService.storeImage(image);

        song.setCoverImage(imagePath);

        Song saved = songRepository.save(song);

        logger.info("Cover image uploaded songId={}", songId);

        return mapToDTO(saved);
    }

    @Override
    public void addSongToAlbum(Long songId, Long albumId, Integer trackNumber) {

        logger.info("Adding song to album songId={} albumId={}", songId, albumId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized album modification attempt songId={}", songId);
            throw new BadRequestException("Unauthorized access");
        }

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        if (!album.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized album ownership attempt albumId={}", albumId);
            throw new BadRequestException("Unauthorized access");
        }

        if (songRepository.existsByAlbumAndId(album, songId)) {
            logger.warn("Song already exists in album songId={} albumId={}", songId, albumId);
            throw new DuplicateResourceException("Song already exists in this album");
        }

        if (songRepository.existsByAlbumAndTrackNumber(album, trackNumber)) {
            logger.info("Track number {} used, finding next available for albumId={}", trackNumber, albumId);
            List<Song> existingSongs = songRepository.findByAlbumOrderByTrackNumberAsc(album);
            if (existingSongs.isEmpty()) {
                trackNumber = 1;
            } else {
                trackNumber = existingSongs.get(existingSongs.size() - 1).getTrackNumber() + 1;
            }
        }

        song.setAlbum(album);
        song.setTrackNumber(trackNumber);

        songRepository.save(song);

        logger.info("Song added to album songId={} albumId={}", songId, albumId);
    }

    @Override
    public List<SongDTO> getAlbumSongs(Long albumId) {

        logger.info("Fetching songs for albumId={}", albumId);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        List<SongDTO> songs = songRepository.findByAlbumOrderByTrackNumberAsc(album)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Album songs fetched count={}", songs.size());

        return songs;
    }

    @Override
    public void removeFromAlbum(Long songId) {

        logger.info("Removing song from album songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized remove from album attempt songId={}", songId);
            throw new BadRequestException("Unauthorized access");
        }

        if (song.getAlbum() == null) {
            logger.warn("Song not in any album songId={}", songId);
            throw new BadRequestException("Song is not in any album");
        }

        song.setAlbum(null);
        song.setTrackNumber(null);

        songRepository.save(song);

        logger.info("Song removed from album songId={}", songId);
    }

    @Override
    public void reorderTrack(Long songId, Integer newTrackNumber) {

        logger.info("Reordering track songId={} newTrackNumber={}", songId, newTrackNumber);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        Album album = song.getAlbum();

        if (album == null) {
            logger.warn("Song not in album songId={}", songId);
            throw new BadRequestException("Song is not in an album");
        }

        List<Song> albumSongs =
                songRepository.findByAlbumOrderByTrackNumberAsc(album);

        int oldTrackNumber = song.getTrackNumber();

        if (newTrackNumber.equals(oldTrackNumber)) {
            logger.debug("Track number unchanged songId={}", songId);
            return;
        }

        for (Song s : albumSongs) {

            if (newTrackNumber < oldTrackNumber) {

                if (s.getTrackNumber() >= newTrackNumber &&
                    s.getTrackNumber() < oldTrackNumber) {
                    s.setTrackNumber(s.getTrackNumber() + 1);
                }

            } else {

                if (s.getTrackNumber() <= newTrackNumber &&
                    s.getTrackNumber() > oldTrackNumber) {
                    s.setTrackNumber(s.getTrackNumber() - 1);
                }
            }
        }

        song.setTrackNumber(newTrackNumber);

        songRepository.saveAll(albumSongs);

        logger.info("Track reordered songId={} newTrackNumber={}", songId, newTrackNumber);
    }

    @Override
    public void deleteSong(Long songId) {

        logger.info("Deleting song songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized delete attempt songId={}", songId);
            throw new BadRequestException("Unauthorized access");
        }

        fileStorageService.deleteFile(song.getAudioPath());

        if (song.getCoverImage() != null) {
            fileStorageService.deleteFile(song.getCoverImage());
        }

        songRepository.delete(song);

        logger.info("Song deleted successfully songId={}", songId);
    }

    private SongDTO mapToDTO(Song song) {

        logger.debug("Mapping song to DTO songId={}", song.getId());

        return new SongDTO(
                song.getId(),
                song.getTitle(),
                song.getGenre(),
                song.getDuration(),
                song.getAudioPath(),
                song.getCoverImage(),
                song.getArtist().getName(),
                song.getAlbum() != null ? song.getAlbum().getName() : null
        );
    }

    @Override
    public List<SongDTO> getAllSongs() {

        logger.info("Fetching all songs");

        List<SongDTO> songs = songRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Total songs fetched count={}", songs.size());

        return songs;
    }

    @Override
    public SongDTO getSongDetails(Long songId) {

        logger.info("Fetching song details songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        return mapToDTO(song);
    }

    @Override
    public List<SongDTO> getPublicSongs() {

        logger.info("Fetching public songs");

        List<SongDTO> songs = songRepository.findByIsPublicTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Public songs fetched count={}", songs.size());

        return songs;
    }

    @Override
    public List<SongFavoriteStatsDTO> getFavoriteStatsForMySongs() {

        logger.info("Fetching favorite stats for artist songs");

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SongFavoriteStatsDTO> stats = songRepository.findByArtist(artist)
                .stream()
                .map(song -> new SongFavoriteStatsDTO(
                        song.getId(),
                        song.getTitle(),
                        favoriteRepository.countBySong(song)
                ))
                .toList();

        logger.info("Favorite stats generated count={}", stats.size());

        return stats;
    }

}