package com.example.demo.repository;


import com.example.demo.entity.Playlist;
import com.example.demo.entity.PlaylistRecording;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRecordingRepository
extends JpaRepository<PlaylistRecording, Long> {

List<PlaylistRecording> findByUser(User user);

List<PlaylistRecording> findByPlaylist(Playlist playlist);


}