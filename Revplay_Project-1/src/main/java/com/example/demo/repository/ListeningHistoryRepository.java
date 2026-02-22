package com.example.demo.repository;

import com.example.demo.entity.ListeningHistory;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {

    List<ListeningHistory> findByListenerOrderByPlayedAtDesc(User listener);

    List<ListeningHistory> findTop10ByListenerOrderByPlayedAtDesc(User listener);
}