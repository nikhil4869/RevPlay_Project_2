package com.example.demo.service;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.playlist.PlaylistDTO;
import java.util.List;

public interface PlaylistService {

    PlaylistDTO createPlaylist(String name, String description, boolean isPublic);

    List<PlaylistDTO> getMyPlaylists();

    List<PlaylistDTO> getPublicPlaylists();

    void deletePlaylist(Long id);
    
    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

    List<SongDTO> getPlaylistSongs(Long playlistId);
    
    void followPlaylist(Long playlistId);

    void unfollowPlaylist(Long playlistId);

    List<PlaylistDTO> getFollowedPlaylists();
}