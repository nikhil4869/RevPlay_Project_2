package com.example.demo.service.impl;

import com.example.demo.dto.music.HistoryDTO;
import com.example.demo.dto.music.ListeningTimeDTO;
import com.example.demo.entity.PlayHistory;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HistoryService;
import com.example.demo.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.dto.music.MostPlayedDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class HistoryServiceImpl implements HistoryService {

    private static final Logger logger = LogManager.getLogger(HistoryServiceImpl.class);

    private final PlayHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public HistoryServiceImpl(PlayHistoryRepository historyRepository,
                              UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;

        logger.info("HistoryServiceImpl initialized");
    }

    private HistoryDTO mapToDTO(PlayHistory history) {

        logger.debug("Mapping PlayHistory to DTO songId={}", history.getSong().getId());

        return new HistoryDTO(
                history.getSong().getId(),
                history.getSong().getTitle(),
                history.getSong().getArtist().getName(),
                history.getSong().getAudioPath(),
                history.getSong().getCoverImage(),
                history.getPlayedAt()
        );
    }

    @Override
    public List<HistoryDTO> getRecentHistory() {

        logger.info("Fetching recent listening history");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PlayHistory> allHistory = historyRepository.findTop50ByUserOrderByPlayedAtDesc(user);

        logger.debug("Fetched {} play history records", allHistory.size());

        java.util.Map<Long, PlayHistory> uniqueSongs = new java.util.LinkedHashMap<>();

        for (PlayHistory ph : allHistory) {
            uniqueSongs.putIfAbsent(ph.getSong().getId(), ph);
        }

        logger.debug("Unique songs after filtering={}", uniqueSongs.size());

        return uniqueSongs.values().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoryDTO> getFullHistory() {

        logger.info("Fetching full listening history");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<HistoryDTO> history = historyRepository
                .findByUserOrderByPlayedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Total history records returned={}", history.size());

        return history;
    }

    @Transactional
    @Override
    public void clearHistory() {

        logger.info("Clearing user listening history");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        historyRepository.deleteByUser(user);

        logger.info("History cleared successfully for userId={}", user.getId());
    }

    @Override
    public ListeningTimeDTO getListeningTime() {

        logger.info("Calculating total listening time");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int totalSeconds = historyRepository.findByUserOrderByPlayedAtDesc(user)
                .stream()
                .mapToInt(PlayHistory::getDurationPlayed)
                .sum();

        logger.info("Total listening time calculated seconds={}", totalSeconds);

        return new ListeningTimeDTO(totalSeconds);
    }

    @Override
    public List<MostPlayedDTO> getMostPlayed() {

        logger.info("Fetching most played songs");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<MostPlayedDTO> mostPlayed = historyRepository.findMostPlayedSongs(user)
                .stream()
                .map(obj -> new MostPlayedDTO(
                        (Long) obj[0],
                        (String) obj[1],
                        (Long) obj[2]
                ))
                .collect(Collectors.toList());

        logger.info("Most played songs count={}", mostPlayed.size());

        return mostPlayed;
    }
}