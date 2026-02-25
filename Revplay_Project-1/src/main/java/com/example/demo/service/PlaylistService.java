package com.example.demo.service;

<<<<<<< HEAD
=======
import com.example.demo.dto.music.SongDTO;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import com.example.demo.dto.playlist.PlaylistDTO;
import java.util.List;

public interface PlaylistService {

<<<<<<< HEAD
    PlaylistDTO createPlaylist(String name);

=======
    PlaylistDTO createPlaylist(String name, String description, boolean isPublic);

    List<PlaylistDTO> getMyPlaylists();

    List<PlaylistDTO> getPublicPlaylists();

    void deletePlaylist(Long id);
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

<<<<<<< HEAD
    List<PlaylistDTO> getMyPlaylists();

    PlaylistDTO getPlaylistWithSongs(Long playlistId);

    
    PlaylistDTO changeVisibility(Long playlistId, boolean isPublic);

    List<PlaylistDTO> getPublicPlaylists();
=======
    List<SongDTO> getPlaylistSongs(Long playlistId);
    
    void followPlaylist(Long playlistId);

    void unfollowPlaylist(Long playlistId);

    List<PlaylistDTO> getFollowedPlaylists();
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}