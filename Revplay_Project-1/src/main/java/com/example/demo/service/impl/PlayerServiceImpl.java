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
<<<<<<< HEAD

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
=======
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
>>>>>>> d4f4593 (Initial commit of RevPlay project)
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
    private static final Logger logger = LogManager.getLogger(PlayerServiceImpl.class);
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
        logger.debug("User {} requested to play song with id: {}", email, songId);

        User user = userRepository.findByEmail(email)
<<<<<<< HEAD
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
=======
                .orElseThrow(() -> {
                    logger.warn("User not found while playing song. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });
>>>>>>> d4f4593 (Initial commit of RevPlay project)

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. Song ID: {}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

<<<<<<< HEAD
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
=======
        // ✅ Count only normal user plays
        if (user.getRole() != null &&
            user.getRole().getName().equalsIgnoreCase("user")) {

            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);

            logger.info("Play count incremented for song '{}' (ID: {}) by user {}",
                    song.getTitle(), songId, email);
        }

        // Save history
>>>>>>> d4f4593 (Initial commit of RevPlay project)
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
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
    
=======
>>>>>>> d4f4593 (Initial commit of RevPlay project)
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