package com.example.demo.service;

<<<<<<< HEAD
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
=======
import java.util.List;

import com.example.demo.dto.music.SongDTO;

public interface PlayerService {

    void playSong(Long songId);
    
//  Get trending songs (by play count)
    List<SongDTO> getTrendingSongs(int limit);

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}