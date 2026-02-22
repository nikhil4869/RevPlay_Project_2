package com.example.demo.repository;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Song;

public interface SongRepository extends JpaRepository<Song, Long>{
=======
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.Album;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByArtist(User artist);
    
    List<Song> findByAlbumOrderByTrackNumberAsc(Album album);
>>>>>>> origin/harish-dev

}
