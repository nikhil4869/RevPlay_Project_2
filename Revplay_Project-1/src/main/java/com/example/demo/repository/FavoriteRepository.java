package com.example.demo.repository;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndSong(User user, Song song);
    
    long countBySong(Song song);
    
    long countByUser(User user);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.song.artist = :artist")
    long countBySongArtist(@org.springframework.data.repository.query.Param("artist") User artist);
}
