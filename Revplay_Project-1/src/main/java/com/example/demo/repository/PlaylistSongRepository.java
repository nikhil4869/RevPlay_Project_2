package com.example.demo.repository;

import com.example.demo.entity.Playlist;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

//    List<PlaylistSong> findByPlaylistOrderByPositionAsc(Playlist playlist);

    Optional<PlaylistSong> findByPlaylistAndSong(Playlist playlist, Song song);

    void deleteByPlaylistAndSong(Playlist playlist, Song song);
}