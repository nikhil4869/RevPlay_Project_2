package com.example.demo.service.impl;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.entity.Album;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.SongService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.dto.music.SongDTO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AlbumRepository albumRepository;

    public SongServiceImpl(SongRepository songRepository,
                           UserRepository userRepository,
                           FileStorageService fileStorageService,
                           AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.albumRepository = albumRepository;
    }

    // 🔥 LISTENER FEATURE (your part)
    @Override
    public List<SongDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🔴 ARTIST FEATURES (Harish code)

    @Override
    public SongDTO uploadSong(String title,
                              String genre,
                              String duration,
                              MultipartFile audioFile,
                              Long albumId,
                              Integer trackNumber) {

        String email = SecurityUtil.getCurrentUserEmail();

        User artist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String filePath = fileStorageService.storeAudio(audioFile);

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre);
        song.setDuration(duration);
        song.setAudioPath(filePath);
        song.setArtist(artist);

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
    public List<SongDTO> getAlbumSongs(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        return songRepository.findByAlbumOrderByTrackNumberAsc(album)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSong(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

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
	public void addSongToAlbum(Long songId, Long albumId, Integer trackNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFromAlbum(Long songId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reorderTrack(Long songId, Integer newTrackNumber) {
		// TODO Auto-generated method stub
		
	}
}