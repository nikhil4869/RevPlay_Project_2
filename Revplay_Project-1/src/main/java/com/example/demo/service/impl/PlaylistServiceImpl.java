package com.example.demo.service.impl;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.dto.playlist.PlaylistRecordingDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.PlaylistService;
import com.example.demo.util.SecurityUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistRecordingRepository playlistRecordingRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            PlaylistRecordingRepository playlistRecordingRepository) {

this.playlistRepository = playlistRepository;
this.playlistSongRepository = playlistSongRepository;
this.songRepository = songRepository;
this.userRepository = userRepository;
this.playlistRecordingRepository = playlistRecordingRepository;
}

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateOwner(Playlist playlist, User user) {
        if (!playlist.getListener().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access");
        }
    }

    // CREATE
    @Override
    public void createPlaylist(String name, boolean isPublic) {

        User user = getCurrentUser();

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setPublic(isPublic);
        playlist.setListener(user);

        playlistRepository.save(playlist);
    }

    // UPDATE
    @Override
    public void updatePlaylist(Long playlistId, String name, boolean isPublic) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        validateOwner(playlist, user);

        playlist.setName(name);
        playlist.setPublic(isPublic);

        playlistRepository.save(playlist);
    }

    // MY PLAYLISTS
    @Override
    public List<PlaylistDTO> getMyPlaylists() {

        User user = getCurrentUser();

        return playlistRepository.findByListener(user)
                .stream()
                .map(p -> new PlaylistDTO(
                        p.getId(),
                        p.getName(),
                        p.isPublic(),
                        p.getFollowers().size()
                ))
                .toList();
    }

    // PUBLIC PLAYLISTS
    @Override
    public List<PlaylistDTO> getPublicPlaylists() {

        return playlistRepository.findByIsPublicTrue()
                .stream()
                .map(p -> new PlaylistDTO(
                        p.getId(),
                        p.getName(),
                        true,
                        p.getFollowers().size()
                ))
                .toList();
    }

    // ADD SONG
    @Override
    public void addSong(Long playlistId, Long songId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        validateOwner(playlist, user);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (playlistSongRepository
                .findByPlaylistAndSong(playlist, song)
                .isPresent()) {
            throw new BadRequestException("Song already in playlist");
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);

        playlistSongRepository.save(ps);
    }

    // REMOVE SONG
    @Override
    public void removeSong(Long playlistId, Long songId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        validateOwner(playlist, user);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        PlaylistSong ps = playlistSongRepository
                .findByPlaylistAndSong(playlist, song)
                .orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));

        playlistSongRepository.delete(ps);
    }

    // FOLLOW
    @Override
    public void followPlaylist(Long playlistId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.isPublic()) {
            throw new BadRequestException("Cannot follow private playlist");
        }

        if (playlist.getFollowers().contains(user)) {
            throw new BadRequestException("Already following");
        }

        playlist.getFollowers().add(user);
        playlistRepository.save(playlist);
    }

    // UNFOLLOW
    @Override
    public void unfollowPlaylist(Long playlistId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        playlist.getFollowers().remove(user);
        playlistRepository.save(playlist);
    }

    // DELETE
    @Override
    public void deletePlaylist(Long playlistId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        validateOwner(playlist, user);

        playlistRepository.delete(playlist);
    }
    
    @Override
    public void recordFromPlaylist(Long playlistId, Long songId) {

        User user = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        playlistSongRepository.findByPlaylistAndSong(playlist, song)
                .orElseThrow(() -> new BadRequestException("Song not in playlist"));

        try {

            // 🔹 Get original static audio path
            String staticBase = System.getProperty("user.dir")
                    + "/src/main/resources/static";

            Path source = Paths.get(staticBase + song.getAudioPath());

            if (!Files.exists(source)) {
                throw new RuntimeException("Original file not found at: " + source);
            }

            // 🔹 Save recording inside uploads/recordings
            String uploadBase = System.getProperty("user.dir")
                    + "/uploads/recordings/";

            Files.createDirectories(Paths.get(uploadBase));

            String newFileName = "recorded_" + System.currentTimeMillis() + ".mp3";

            Path target = Paths.get(uploadBase + newFileName);

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            PlaylistRecording recording = new PlaylistRecording();
            recording.setUser(user);
            recording.setPlaylist(playlist);
            recording.setSong(song);
            recording.setFilePath("/uploads/recordings/" + newFileName);
            recording.setDurationSeconds(0);

            playlistRecordingRepository.save(recording);

        } catch (IOException e) {
            throw new RuntimeException("Recording failed: " + e.getMessage());
        }
    }
    
    public List<PlaylistRecordingDTO> getPlaylistRecordings(Long playlistId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        List<PlaylistRecording> recordings =
                playlistRecordingRepository.findByPlaylist(playlist);

        return recordings.stream()
                .map(r -> new PlaylistRecordingDTO(
                        r.getId(),
                        r.getSong().getTitle() + " [Recorded]",  
                        r.getFilePath()
                ))
                .toList();
    }
    
    @Override
    public void deleteRecording(Long recordingId) {

        User user = getCurrentUser();

        PlaylistRecording recording =
                playlistRecordingRepository.findById(recordingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Recording not found"));

        //  Only owner can delete
        if (!recording.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized access");
        }

        // Delete physical file
        try {
            String basePath = System.getProperty("user.dir");
            Path filePath = Paths.get(basePath + recording.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete recording file");
        }

        // Delete DB record
        playlistRecordingRepository.delete(recording);
    }

}