package com.example.demo.service;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.dto.playlist.PlaylistRecordingDTO;

import java.util.List;

public interface PlaylistService {

    void createPlaylist(String name, boolean isPublic);

    void updatePlaylist(Long playlistId, String name, boolean isPublic);

    List<PlaylistDTO> getMyPlaylists();

    List<PlaylistDTO> getPublicPlaylists();

    void addSong(Long playlistId, Long songId);

    void removeSong(Long playlistId, Long songId);

//    void reorderSong(Long playlistId, Long songId, int newPosition);
    void recordFromPlaylist(Long playlistId, Long songId);
    
    List<PlaylistRecordingDTO> getPlaylistRecordings(Long playlistId);

    void followPlaylist(Long playlistId);

    void unfollowPlaylist(Long playlistId);

    void deletePlaylist(Long playlistId);
    
    void deleteRecording(Long recordingId);
}