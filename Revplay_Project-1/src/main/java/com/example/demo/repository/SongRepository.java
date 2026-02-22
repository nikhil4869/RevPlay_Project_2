package com.example.demo.repository;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    //  Artist module
    List<Song> findByArtist(User artist);

    List<Song> findByAlbumOrderByTrackNumberAsc(Album album);

    //  Listener module

    // Public songs
    List<Song> findByIsPublicTrue();

    // Search by title
    List<Song> findByTitleContainingIgnoreCaseAndIsPublicTrue(String title);
    
    List<Song> findByIsPublicTrueOrderByPlayCountDesc();

    // Search by genre
    List<Song> findByGenreContainingIgnoreCaseAndIsPublicTrue(String genre);

    //  Search by artist name
    List<Song> findByArtist_NameContainingIgnoreCaseAndIsPublicTrue(String artistName);

    //  Most played songs
    @Query("SELECT s FROM Song s WHERE s.isPublic = true ORDER BY s.playCount DESC")
    List<Song> findMostPlayedSongs(Pageable pageable);

    //  Recently added songs
    @Query("SELECT s FROM Song s WHERE s.isPublic = true ORDER BY s.createdAt DESC")
    List<Song> findRecentlyAddedSongs(Pageable pageable);
}