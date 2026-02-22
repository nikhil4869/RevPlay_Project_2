package com.example.demo.service;

import com.example.demo.dto.playlist.PlaylistDTO;
import java.util.List;

public interface PlaylistService {

    PlaylistDTO createPlaylist(String name);

    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

    List<PlaylistDTO> getMyPlaylists();

    PlaylistDTO getPlaylistWithSongs(Long playlistId);

    
    PlaylistDTO changeVisibility(Long playlistId, boolean isPublic);

    List<PlaylistDTO> getPublicPlaylists();
}