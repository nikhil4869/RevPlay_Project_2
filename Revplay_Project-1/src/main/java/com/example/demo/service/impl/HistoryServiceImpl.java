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

    private final PlayHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(HistoryServiceImpl.class);
    public HistoryServiceImpl(PlayHistoryRepository historyRepository,
                              UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    private HistoryDTO mapToDTO(PlayHistory history) {
        return new HistoryDTO(
                history.getSong().getId(),
                history.getSong().getTitle(),
                history.getSong().getArtist().getName(),
                history.getSong().getAudioPath(),
                history.getPlayedAt()
        );
    }

    @Override
    public List<HistoryDTO> getRecentHistory() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Fetching recent history for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching recent history: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<HistoryDTO> history = historyRepository
                .findTop50ByUserOrderByPlayedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Recent history fetched for user: {} - Records: {}", email, history.size());

        return history;
    }

    @Override
    public List<HistoryDTO> getFullHistory() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Fetching full history for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching full history: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<HistoryDTO> history = historyRepository
                .findByUserOrderByPlayedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        logger.info("Full history fetched for user: {} - Records: {}", email, history.size());

        return history;
    }

    @Transactional
    @Override
    public void clearHistory() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Clearing listening history for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while clearing history: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        historyRepository.deleteByUser(user);

        logger.info("Listening history cleared successfully for user: {}", email);
    }
    
    @Override
    public ListeningTimeDTO getListeningTime() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Calculating listening time for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while calculating listening time: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        int totalSeconds = historyRepository.findByUserOrderByPlayedAtDesc(user)
                .stream()
                .mapToInt(PlayHistory::getDurationPlayed)
                .sum();

        logger.info("Total listening time for user {}: {} seconds", email, totalSeconds);

        return new ListeningTimeDTO(totalSeconds);
    }
    
    @Override
    public List<MostPlayedDTO> getMostPlayed() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Fetching most played songs for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching most played songs: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<MostPlayedDTO> mostPlayed = historyRepository.findMostPlayedSongs(user)
                .stream()
                .map(obj -> new MostPlayedDTO(
                        (Long) obj[0],
                        (String) obj[1],
                        (Long) obj[2]
                ))
                .collect(Collectors.toList());

        logger.info("Most played songs fetched for user: {} - Records: {}", email, mostPlayed.size());

        return mostPlayed;
    }
}