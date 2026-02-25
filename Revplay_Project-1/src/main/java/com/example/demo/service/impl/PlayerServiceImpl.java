package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
<<<<<<< HEAD
import com.example.demo.entity.ListeningHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ListeningHistoryRepository;
=======
import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayHistoryRepository;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PlayerService;
import com.example.demo.util.SecurityUtil;
<<<<<<< HEAD

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

=======
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
@Service
public class PlayerServiceImpl implements PlayerService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
<<<<<<< HEAD
    private final ListeningHistoryRepository historyRepository;

    public PlayerServiceImpl(SongRepository songRepository,
                             UserRepository userRepository,
                             ListeningHistoryRepository historyRepository) {
=======
    private final PlayHistoryRepository historyRepository;

    public PlayerServiceImpl(SongRepository songRepository,
                             UserRepository userRepository,
                             PlayHistoryRepository historyRepository) {
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
<<<<<<< HEAD
    public SongDTO playSong(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
=======
    public void playSong(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

<<<<<<< HEAD
        if (!song.isPublic()) {
            throw new RuntimeException("Song is not public");
        }

=======
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
        // increment play count
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);

        // save history
<<<<<<< HEAD
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

=======
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
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
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
<<<<<<< HEAD
=======
    
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}