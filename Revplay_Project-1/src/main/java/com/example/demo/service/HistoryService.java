package com.example.demo.service;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface HistoryService {

    void recordPlay(Long songId);

    List<SongDTO> getMyHistory();

    List<SongDTO> getRecentlyPlayed();
    
    List<SongDTO> getMyHistory(int page, int size);

    void clearMyHistory();
}