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

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AlbumRepository albumRepository;
    private final FavoriteRepository favoriteRepository;
    private static final Logger logger =
            LogManager.getLogger(SongServiceImpl.class);


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
}


    @Override
    public SongDTO uploadSong(String title,
                              String genre,
                              String duration,
                              MultipartFile audioFile,
                              Long albumId,
                              Integer trackNumber,
                              Integer releaseYear) {

        logger.debug("Uploading new song: title='{}', albumId={}", title, albumId);

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while uploading song. email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (songRepository.existsByTitleIgnoreCaseAndArtist(title, artist)) {
            logger.warn("Duplicate song upload attempt: title='{}'", title);
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
                    .orElseThrow(() -> {
                        logger.warn("Album not found while uploading song. albumId={}", albumId);
                        return new ResourceNotFoundException("Album not found");
                    });

            song.setAlbum(album);
            song.setTrackNumber(trackNumber);
        }

        Song saved = songRepository.save(song);

        logger.info("Song uploaded successfully. id={}, title='{}'", saved.getId(), title);

        return mapToDTO(saved);
    }

    @Override
    public List<SongDTO> getMySongs() {

        logger.debug("Fetching songs for current artist");

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching songs. email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<SongDTO> songs = songRepository.findByArtist(artist)
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Fetched {} songs for artist={}", songs.size(), email);

        return songs;
    }
    
    @Override
    public SongDTO uploadCover(Long songId, MultipartFile image) {

        logger.debug("Uploading cover for songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found for cover upload. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized cover upload attempt. songId={}, user={}", songId, email);
            throw new BadRequestException("Unauthorized access");
        }

        String imagePath = fileStorageService.storeImage(image);
        song.setCoverImage(imagePath);

        Song saved = songRepository.save(song);

        logger.info("Cover uploaded successfully for songId={}", songId);

        return mapToDTO(saved);
    }
    
    @Override
    public void addSongToAlbum(Long songId, Long albumId, Integer trackNumber) {

        logger.debug("Adding songId={} to albumId={} with trackNumber={}", 
                     songId, albumId, trackNumber);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found. email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized album add attempt. songId={}, user={}", songId, email);
            throw new BadRequestException("Unauthorized access");
        }

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> {
                    logger.warn("Album not found. id={}", albumId);
                    return new ResourceNotFoundException("Album not found");
                });

        if (!album.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized album access. albumId={}, user={}", albumId, email);
            throw new BadRequestException("Unauthorized access");
        }

        if (songRepository.existsByAlbumAndId(album, songId)) {
            logger.warn("Song already exists in album. songId={}, albumId={}", songId, albumId);
            throw new DuplicateResourceException("Song already exists in this album");
        }

        if (songRepository.existsByAlbumAndTrackNumber(album, trackNumber)) {
            logger.warn("Duplicate track number {} in albumId={}", trackNumber, albumId);
            throw new DuplicateResourceException("Track number already used in this album");
        }

        song.setAlbum(album);
        song.setTrackNumber(trackNumber);

        songRepository.save(song);

        logger.info("Song {} successfully added to album {}", songId, albumId);
    }
    @Override
    public List<SongDTO> getAlbumSongs(Long albumId) {

        logger.debug("Fetching songs for albumId={}", albumId);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> {
                    logger.warn("Album not found. id={}", albumId);
                    return new ResourceNotFoundException("Album not found");
                });

        List<SongDTO> songs = songRepository
                .findByAlbumOrderByTrackNumberAsc(album)
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Fetched {} songs for albumId={}", songs.size(), albumId);

        return songs;
    }
    @Override
    public void removeFromAlbum(Long songId) {

        logger.debug("Removing songId={} from album", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found. email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized album removal attempt. songId={}, user={}", songId, email);
            throw new BadRequestException("Unauthorized access");
        }

        if (song.getAlbum() == null) {
            logger.warn("Song {} is not part of any album", songId);
            throw new BadRequestException("Song is not in any album");
        }

        song.setAlbum(null);
        song.setTrackNumber(null);

        songRepository.save(song);

        logger.info("Song {} successfully removed from album", songId);
    }


    @Override
    public void reorderTrack(Long songId, Integer newTrackNumber) {

        logger.debug("Reordering track. songId={}, newTrackNumber={}", 
                     songId, newTrackNumber);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        Album album = song.getAlbum();

        if (album == null) {
            logger.warn("Song {} is not in any album", songId);
            throw new BadRequestException("Song is not in an album");
        }

        List<Song> albumSongs =
                songRepository.findByAlbumOrderByTrackNumberAsc(album);

        int oldTrackNumber = song.getTrackNumber();

        if (newTrackNumber.equals(oldTrackNumber)) {
            logger.debug("Track number unchanged for songId={}", songId);
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

        logger.info("Track reordered successfully. songId={}, newTrackNumber={}",
                    songId, newTrackNumber);
    }
    
    @Override
    public void deleteSong(Long songId) {

        logger.debug("Attempting to delete song id={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found for deletion. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized song delete attempt. songId={}, user={}", songId, email);
            throw new BadRequestException("Unauthorized access");
        }

        fileStorageService.deleteFile(song.getAudioPath());

        if (song.getCoverImage() != null) {
            fileStorageService.deleteFile(song.getCoverImage());
        }

        songRepository.delete(song);

        logger.info("Song deleted successfully. id={}, title='{}'", songId, song.getTitle());
    }
    
    @Override
    public List<SongDTO> getAllSongs() {

        logger.debug("Fetching all songs");

        List<SongDTO> songs = songRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Fetched {} total songs", songs.size());

        return songs;
    }
    
    @Override
    public SongDTO getSongDetails(Long songId) {

        logger.debug("Fetching details for songId={}", songId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. id={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        logger.info("Fetched song details for id={}", songId);

        return mapToDTO(song);
    }
    @Override
    public List<SongDTO> getPublicSongs() {

        logger.debug("Fetching public songs");

        List<SongDTO> songs = songRepository.findByIsPublicTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();

        logger.info("Fetched {} public songs", songs.size());

        return songs;
    }


    @Override
    public List<SongFavoriteStatsDTO> getFavoriteStatsForMySongs() {

        logger.debug("Fetching favorite stats for current artist");

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching favorite stats. email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<SongFavoriteStatsDTO> stats = songRepository.findByArtist(artist)
                .stream()
                .map(song -> new SongFavoriteStatsDTO(
                        song.getId(),
                        song.getTitle(),
                        favoriteRepository.countBySong(song)
                ))
                .toList();

        logger.info("Fetched favorite stats for {} songs", stats.size());

        return stats;
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
}
