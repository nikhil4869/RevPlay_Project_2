package com.example.demo.repository;

import com.example.demo.entity.Playlist;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    boolean existsByPlaylistAndSong(Playlist playlist, Song song);

    List<PlaylistSong> findByPlaylist(Playlist playlist);

    long countByPlaylist(Playlist playlist);
}