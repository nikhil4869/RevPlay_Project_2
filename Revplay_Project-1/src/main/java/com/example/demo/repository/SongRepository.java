package com.example.demo.repository;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
<<<<<<< HEAD
import com.example.demo.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import com.example.demo.entity.Album;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByArtist(User artist);
    
    List<Song> findByAlbumOrderByTrackNumberAsc(Album album);
    
    List<Song> findByIsPublicTrue();
    
    List<Song> findByTitleContainingIgnoreCaseAndIsPublicTrue(String keyword);
    
    @Query("SELECT DISTINCT s.genre FROM Song s")
    List<String> findAllGenres();

    List<Song> findByGenreIgnoreCaseContaining(String genre);

    long countByArtist(User artist);

    @Query("SELECT COALESCE(SUM(s.playCount),0) FROM Song s WHERE s.artist = :artist")
    long sumPlayCountByArtist(User artist);

    List<Song> findTop5ByArtistOrderByPlayCountDesc(User artist);
    
    List<Song> findByReleaseYear(Integer releaseYear);
    
    List<Song> findByIsPublicTrueOrderByPlayCountDesc();
    
    boolean existsByTitleIgnoreCaseAndArtist(String title, User artist);
    
    boolean existsByAlbumAndId(Album album, Long songId);

    boolean existsByAlbumAndTrackNumber(Album album, Integer trackNumber);
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

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