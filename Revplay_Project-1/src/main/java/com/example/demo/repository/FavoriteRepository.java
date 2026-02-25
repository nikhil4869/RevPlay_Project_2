package com.example.demo.repository;

import com.example.demo.entity.Favorite;
<<<<<<< HEAD
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import com.example.demo.entity.User;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

<<<<<<< HEAD
    Optional<Favorite> findByListenerAndSong(User listener, Song song);

    List<Favorite> findByListener(User listener);
}
=======
    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndSong(User user, Song song);
    
    long countBySong(Song song);
    
    long countByUser(User user);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.song.artist = :artist")
    long countBySongArtist(@org.springframework.data.repository.query.Param("artist") User artist);
}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
