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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger logger = LogManager.getLogger(PlayerServiceImpl.class);

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlayHistoryRepository historyRepository;

    public PlayerServiceImpl(SongRepository songRepository,
                             UserRepository userRepository,
                             PlayHistoryRepository historyRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;

        logger.info("PlayerServiceImpl initialized");
    }

    @Override
    public void playSong(Long songId) {

        logger.info("Play song request received songId={}", songId);

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // ✅ Only count plays from USERS (not artists)
        if (user.getRole() != null &&
            user.getRole().getName().equalsIgnoreCase("user")) {

            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);

            logger.debug("Play count incremented songId={} newCount={}",
                    song.getId(), song.getPlayCount());
        }

        // Save history for both user and artist
        PlayHistory history = new PlayHistory();
        history.setUser(user);
        history.setSong(song);
        history.setPlayedAt(LocalDateTime.now());

        String duration = song.getDuration();
        int seconds = 0;

        if (duration != null && duration.contains(":")) {
            String[] parts = duration.split(":");
            seconds = Integer.parseInt(parts[0]) * 60
                    + Integer.parseInt(parts[1]);
        }

        history.setDurationPlayed(seconds);

        historyRepository.save(history);

        logger.info("Play history saved userId={} songId={} secondsPlayed={}",
                user.getId(), song.getId(), seconds);
    }

    @Override
    public List<SongDTO> getTrendingSongs(int limit) {

        logger.info("Fetching trending songs limit={}", limit);

        List<SongDTO> trendingSongs = songRepository
                .findByIsPublicTrueOrderByPlayCountDesc()
                .stream()
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Trending songs fetched count={}", trendingSongs.size());

        return trendingSongs;
    }

    private SongDTO mapToDTO(Song song) {

        logger.debug("Mapping Song to DTO songId={}", song.getId());

        return new SongDTO(
                song.getId(),
                song.getTitle(),
                song.getGenre(),
                song.getDuration(),
                song.getAudioPath(),
                song.getCoverImage(),
                song.getArtist().getName(),
                song.getAlbum() != null ? song.getAlbum().getName() : null
        );
    }
}