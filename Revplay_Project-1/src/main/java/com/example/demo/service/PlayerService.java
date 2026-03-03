package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.music.SongDTO;

public interface PlayerService {

    void playSong(Long songId);
    
//  Get trending songs (by play count)
    List<SongDTO> getTrendingSongs(int limit);

}