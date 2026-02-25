package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PlayerService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlayHistoryRepository historyRepository;

    public PlayerServiceImpl(SongRepository songRepository,
                             UserRepository userRepository,
                             PlayHistoryRepository historyRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public void playSong(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // increment play count
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);

        // save history
        PlayHistory history = new PlayHistory();
        history.setUser(user);
        history.setSong(song);
        history.setPlayedAt(LocalDateTime.now());

        // convert duration "4:30" → seconds
        String duration = song.getDuration();
        int seconds = 0;

        if (duration != null && duration.contains(":")) {
            String[] parts = duration.split(":");
            seconds = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        }

        history.setDurationPlayed(seconds);

        historyRepository.save(history);
    }
    
    @Override
    public List<SongDTO> getTrendingSongs(int limit) {

        // For now trending = most played (simple logic)
        // Later we can improve with weekly trend

        return songRepository
                .findByIsPublicTrueOrderByPlayCountDesc()
                .stream()
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private SongDTO mapToDTO(Song song) {
        return new SongDTO(
                song.getId(),
                song.getTitle(),
                song.getGenre(),
                song.getDuration(),
                song.getAudioPath(),
                song.getCoverImage(),
                song.getArtist().getName()
        );
    }
}