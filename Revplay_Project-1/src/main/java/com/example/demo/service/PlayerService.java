package com.example.demo.service;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface PlayerService {

    //  Play a song (increments play count + saves history)
    SongDTO playSong(Long songId);

    //  Get current listener full history
    List<SongDTO> getMyHistory();

    //  Get last 10 recently played songs
    List<SongDTO> getRecentlyPlayed(int limit);

    //  Get most played songs globally
    List<SongDTO> getMostPlayedSongs(int limit);

    //  Get trending songs (by play count)
    List<SongDTO> getTrendingSongs(int limit);
}