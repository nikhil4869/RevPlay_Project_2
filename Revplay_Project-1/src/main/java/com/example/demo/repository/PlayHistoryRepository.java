package com.example.demo.repository;

import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    List<PlayHistory> findTop50ByUserOrderByPlayedAtDesc(User user);

    List<PlayHistory> findByUserOrderByPlayedAtDesc(User user);

    void deleteByUser(User user);
    
    @Query("""
           SELECT h.song.id, h.song.title, COUNT(h)
           FROM PlayHistory h
           WHERE h.user = :user
           GROUP BY h.song.id, h.song.title
           ORDER BY COUNT(h) DESC
           """)
    List<Object[]> findMostPlayedSongs(@Param("user") User user);
    
    long countByUser(User user);

    @Query("SELECT COALESCE(SUM(p.durationPlayed),0) FROM PlayHistory p WHERE p.user = :user")
    long getTotalListeningTime(User user);
    
    @Query("""
    	       SELECT COUNT(DISTINCT h.user.id)
    	       FROM PlayHistory h
    	       WHERE h.song.artist = :artist
    	       """)
    	long countUniqueListeners(@Param("artist") User artist);
    
    @Query(value = """
    	       SELECT TRUNC(ph.played_at) AS play_date,
    	              COUNT(ph.id) AS play_count
    	       FROM PLAY_HISTORY ph
    	       JOIN SONG s ON s.id = ph.song_id
    	       WHERE s.artist_id = :artistId
    	       GROUP BY TRUNC(ph.played_at)
    	       ORDER BY TRUNC(ph.played_at)
    	       """, nativeQuery = true)
    	List<Object[]> getDailyPlayCounts(@Param("artistId") Long artistId);
}