package com.example.demo.service.impl;

import com.example.demo.entity.Album;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
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

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public AlbumServiceImpl(AlbumRepository albumRepository,
                            UserRepository userRepository,
                            FileStorageService fileStorageService) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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


    @Override
    public AlbumDTO createAlbum(String name,
                                String description,
                                LocalDate releaseDate) {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 🚫 prevent duplicate albums
        if (albumRepository.existsByNameIgnoreCaseAndArtist(name, artist)) {
            throw new DuplicateResourceException("Album with this name already exists");
        }

        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setReleaseDate(releaseDate);
        album.setArtist(artist);

        return mapToDTO(albumRepository.save(album));
    }

    @Override
    public AlbumDTO uploadCover(Long albumId, MultipartFile image) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!album.getArtist().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        String imagePath = fileStorageService.storeImage(image);
        album.setCoverImage(imagePath);

        return mapToDTO(albumRepository.save(album));
    }

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
    
    @Override
    public AlbumDTO getAlbumDetails(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        return mapToDTO(album);
    }

    @Override
    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


}