package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.ListeningHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ListeningHistoryRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PlayerService;
import com.example.demo.util.SecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final ListeningHistoryRepository historyRepository;

    public PlayerServiceImpl(SongRepository songRepository,
                             UserRepository userRepository,
                             ListeningHistoryRepository historyRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public SongDTO playSong(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.isPublic()) {
            throw new RuntimeException("Song is not public");
        }

        // increment play count
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);

        // save history
        ListeningHistory history = new ListeningHistory();
        history.setListener(listener);
        history.setSong(song);

        historyRepository.save(history);

        return mapToDTO(song);
    }

    @Override
    public List<SongDTO> getMyHistory() {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository
                .findByListenerOrderByPlayedAtDesc(listener)
                .stream()
                .map(h -> mapToDTO(h.getSong()))
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

    @Override
    public List<SongDTO> getRecentlyPlayed(int limit) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository
                .findByListenerOrderByPlayedAtDesc(listener)
                .stream()
                .limit(limit)
                .map(h -> mapToDTO(h.getSong()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDTO> getMostPlayedSongs(int limit) {

        return songRepository
                .findByIsPublicTrueOrderByPlayCountDesc()
                .stream()
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
}