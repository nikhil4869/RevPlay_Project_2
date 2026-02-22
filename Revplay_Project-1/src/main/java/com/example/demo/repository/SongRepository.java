package com.example.demo.repository;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.Album;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByArtist(User artist);
    
    List<Song> findByAlbumOrderByTrackNumberAsc(Album album);

}
