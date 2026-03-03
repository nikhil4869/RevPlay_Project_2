package com.example.demo.repository;

import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findByPlaylistOrderByPosition(Playlist playlist);
    

    List<PlaylistSong> findByPlaylistOrderByPositionAsc(Playlist playlist);

    Optional<PlaylistSong> findByPlaylistAndSongId(Playlist playlist, Long songId);
}