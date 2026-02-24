package com.example.demo.repository;

import com.example.demo.entity.ListeningHistory;
import com.example.demo.entity.User;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {

    List<ListeningHistory> findByListenerOrderByPlayedAtDesc(User listener);
    
    long countByListener(User listener);

    void deleteByListener(User listener);

    List<ListeningHistory> findByListener(User listener);

    List<ListeningHistory> findTop5ByListenerOrderByPlayedAtDesc(User listener);
    

    Page<ListeningHistory> findByListenerOrderByPlayedAtDesc(User listener, Pageable pageable);

    
}