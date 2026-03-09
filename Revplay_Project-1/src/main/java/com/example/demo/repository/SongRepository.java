package com.example.demo.repository;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
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
    
    @Query("SELECT DISTINCT s.releaseYear FROM Song s WHERE s.releaseYear IS NOT NULL ORDER BY s.releaseYear DESC")
    List<Integer> findAllReleaseYears();

    List<Song> findByGenreIgnoreCaseContaining(String genre);

    List<Song> findByGenreIgnoreCaseContainingAndIsPublicTrue(String genre);

    long countByArtist(User artist);

    @Query("SELECT COALESCE(SUM(s.playCount),0) FROM Song s WHERE s.artist = :artist")
    long sumPlayCountByArtist(User artist);

    List<Song> findTop5ByArtistOrderByPlayCountDesc(User artist);
    
    List<Song> findByReleaseYear(Integer releaseYear);

    List<Song> findByReleaseYearAndIsPublicTrue(Integer releaseYear);
    
    List<Song> findByIsPublicTrueOrderByPlayCountDesc();
    
    boolean existsByTitleIgnoreCaseAndArtist(String title, User artist);
    
    boolean existsByAlbumAndId(Album album, Long songId);

    boolean existsByAlbumAndTrackNumber(Album album, Integer trackNumber);
    
    List<Song> findByAlbum(Album album);

    List<Song> findByAlbum_IdAndIsPublicTrue(Long albumId);

    List<Song> findByArtist_IdAndIsPublicTrue(Long artistId);

    @Query("SELECT s FROM Song s WHERE s.isPublic = true AND (" +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.artist.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.genre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(s.releaseYear AS string) LIKE CONCAT('%', :keyword, '%'))")
    List<Song> searchPublicSongs(@org.springframework.data.repository.query.Param("keyword") String keyword);

}
