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

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlayHistoryRepository historyRepository;
    private static final Logger logger = LogManager.getLogger(PlayerServiceImpl.class);
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
        logger.debug("User {} requested to play song with id: {}", email, songId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while playing song. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. Song ID: {}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        // ✅ Count only normal user plays
        if (user.getRole() != null &&
            user.getRole().getName().equalsIgnoreCase("user")) {

            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);

            logger.info("Play count incremented for song '{}' (ID: {}) by user {}",
                    song.getTitle(), songId, email);
        }

        // Save history
        PlayHistory history = new PlayHistory();
        history.setUser(user);
        history.setSong(song);
        history.setPlayedAt(LocalDateTime.now());

        String duration = song.getDuration();
        int seconds = 0;

        if (duration != null && duration.contains(":")) {
            try {
                String[] parts = duration.split(":");
                seconds = Integer.parseInt(parts[0]) * 60
                        + Integer.parseInt(parts[1]);
            } catch (Exception e) {
                logger.error("Invalid duration format for song '{}' (ID: {})",
                        song.getTitle(), songId, e);
            }
        }

        history.setDurationPlayed(seconds);
        historyRepository.save(history);

        logger.info("Play history saved for user {} and song '{}'",
                email, song.getTitle());
    }
    
    @Override
    public List<SongDTO> getTrendingSongs(int limit) {

        logger.debug("Fetching trending songs with limit: {}", limit);

        List<SongDTO> trending = songRepository
                .findByIsPublicTrueOrderByPlayCountDesc()
                .stream()
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Trending songs fetched. Total returned: {}", trending.size());

        return trending;
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