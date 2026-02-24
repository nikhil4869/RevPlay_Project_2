package com.example.demo.service.impl;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.AlbumDetailsDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AlbumService;
import com.example.demo.service.FileStorageService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final SongRepository songRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository,
                            UserRepository userRepository,
                            FileStorageService fileStorageService,
                            SongRepository songRepository) {

        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.songRepository = songRepository;
    }

   
    private AlbumDTO mapToDTO(Album album) {
        return new AlbumDTO(
                album.getId(),
                album.getName(),
                album.getDescription(),
                album.getReleaseDate(),
                album.getCoverImage(),
                album.getArtist().getName()
        );
    }

  
    private SongDTO mapSongToDTO(Song song) {
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

   
    // Create Album
    
    @Override
    public AlbumDTO createAlbum(String name,
                                String description,
                                LocalDate releaseDate) {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setReleaseDate(releaseDate);
        album.setArtist(artist);

        return mapToDTO(albumRepository.save(album));
    }

   
    // Upload Cover
   
    @Override
    public AlbumDTO uploadCover(Long albumId, MultipartFile image) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Only owner can upload cover
        if (!album.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }

        String imagePath = fileStorageService.storeImage(image);
        album.setCoverImage(imagePath);

        return mapToDTO(albumRepository.save(album));
    }

 
    // Get My Albums (Artist)
   
    @Override
    public List<AlbumDTO> getMyAlbums() {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return albumRepository.findByArtist(artist)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

  
    // Album Full Details (Listener Access)
    
    @Override
    public AlbumDetailsDTO getAlbumDetails(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Album not found"));

        List<SongDTO> tracks = songRepository
                .findByAlbumOrderByTrackNumberAsc(album)
                .stream()
                .map(this::mapSongToDTO)
                .toList();

        AlbumDetailsDTO dto = new AlbumDetailsDTO();
        dto.setAlbumName(album.getName());
        dto.setArtistName(album.getArtist().getName());
        dto.setReleaseYear(album.getReleaseDate().getYear());
        dto.setTracks(tracks);

        return dto;
    }
}