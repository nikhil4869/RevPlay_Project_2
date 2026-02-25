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


@Service
public class SongServiceImpl implements SongService {

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
}


    @Override
    public SongDTO uploadSong(String title,
            String genre,
            String duration,
            MultipartFile audioFile,
            Long albumId,
            Integer trackNumber,
            Integer releaseYear) {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (songRepository.existsByTitleIgnoreCaseAndArtist(title, artist)) {
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
        
     //  if album provided, attach song to album
        if (albumId != null) {

            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

            song.setAlbum(album);
            song.setTrackNumber(trackNumber);
        }


        return mapToDTO(songRepository.save(song));
    }

    @Override
    public List<SongDTO> getMySongs() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return songRepository.findByArtist(artist)
                .stream()
                .map(this::mapToDTO)
                .toList();

    }
    
    @Override
    public SongDTO uploadCover(Long songId, MultipartFile image) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        String imagePath = fileStorageService.storeImage(image);

        song.setCoverImage(imagePath);

        return mapToDTO(songRepository.save(song));

    }
    
    @Override
    public void addSongToAlbum(Long songId, Long albumId, Integer trackNumber) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        if (!album.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }

        // 🚫 prevent same song added twice
        if (songRepository.existsByAlbumAndId(album, songId)) {
            throw new DuplicateResourceException("Song already exists in this album");
        }

        // 🚫 prevent duplicate track numbers
        if (songRepository.existsByAlbumAndTrackNumber(album, trackNumber)) {
            throw new DuplicateResourceException("Track number already used in this album");
        }

        song.setAlbum(album);
        song.setTrackNumber(trackNumber);

        songRepository.save(song);
    }

    @Override
    public List<SongDTO> getAlbumSongs(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        return songRepository.findByAlbumOrderByTrackNumberAsc(album)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromAlbum(Long songId) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        if (song.getAlbum() == null) {
            throw new BadRequestException("Song is not in any album");
        }

        song.setAlbum(null);
        song.setTrackNumber(null);

        songRepository.save(song);
    }


    @Override
    public void reorderTrack(Long songId, Integer newTrackNumber) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        Album album = song.getAlbum();

        if (album == null) {
            throw new BadRequestException("Song is not in an album");
        }

        List<Song> albumSongs =
                songRepository.findByAlbumOrderByTrackNumberAsc(album);

        int oldTrackNumber = song.getTrackNumber();

        if (newTrackNumber.equals(oldTrackNumber)) {
            return;
        }

        for (Song s : albumSongs) {

            if (newTrackNumber < oldTrackNumber) {
                // moving UP
                if (s.getTrackNumber() >= newTrackNumber &&
                    s.getTrackNumber() < oldTrackNumber) {
                    s.setTrackNumber(s.getTrackNumber() + 1);
                }
            } else {
                // moving DOWN
                if (s.getTrackNumber() <= newTrackNumber &&
                    s.getTrackNumber() > oldTrackNumber) {
                    s.setTrackNumber(s.getTrackNumber() - 1);
                }
            }
        }

        song.setTrackNumber(newTrackNumber);

        songRepository.saveAll(albumSongs);
    }
    
    @Override
    public void deleteSong(Long songId) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        // delete audio file
        fileStorageService.deleteFile(song.getAudioPath());

        // delete cover image
        if (song.getCoverImage() != null) {
            fileStorageService.deleteFile(song.getCoverImage());
        }

        songRepository.delete(song);
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
    public List<SongDTO> getAllSongs() {

        return songRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    
    @Override
    public SongDTO getSongDetails(Long songId) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        return mapToDTO(song);
    }
    
    @Override
    public List<SongDTO> getPublicSongs() {

        return songRepository.findByIsPublicTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<SongFavoriteStatsDTO> getFavoriteStatsForMySongs() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return songRepository.findByArtist(artist)
                .stream()
                .map(song -> new SongFavoriteStatsDTO(
                        song.getId(),
                        song.getTitle(),
                        favoriteRepository.countBySong(song)
                ))
                .toList();
    }

}
