package com.example.demo.repository;

<<<<<<< HEAD
import com.example.demo.entity.Playlist;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    boolean existsByPlaylistAndSong(Playlist playlist, Song song);

    List<PlaylistSong> findByPlaylist(Playlist playlist);

    long countByPlaylist(Playlist playlist);
=======
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findByPlaylistOrderByPosition(Playlist playlist);
    

    List<PlaylistSong> findByPlaylistOrderByPositionAsc(Playlist playlist);

    Optional<PlaylistSong> findByPlaylistAndSongId(Playlist playlist, Long songId);
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}