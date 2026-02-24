package com.example.demo.service.impl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.ListeningHistory;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ListeningHistoryRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HistoryService;
import com.example.demo.util.SecurityUtil;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final ListeningHistoryRepository historyRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public HistoryServiceImpl(ListeningHistoryRepository historyRepository,
                              SongRepository songRepository,
                              UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }
    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void recordPlay(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        ListeningHistory history = new ListeningHistory();
        history.setListener(listener);
        history.setSong(song);

        historyRepository.save(history);
    }

    @Override
    public List<SongDTO> getMyHistory() {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository.findByListenerOrderByPlayedAtDesc(listener)
                .stream()
                .map(h -> new SongDTO(
                        h.getSong().getId(),
                        h.getSong().getTitle(),
                        h.getSong().getGenre(),
                        h.getSong().getDuration(),
                        h.getSong().getAudioPath(),
                        h.getSong().getCoverImage(),
                        h.getSong().getArtist().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDTO> getRecentlyPlayed() {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return historyRepository
                .findTop5ByListenerOrderByPlayedAtDesc(listener)
                .stream()
                .map(h -> new SongDTO(
                        h.getSong().getId(),
                        h.getSong().getTitle(),
                        h.getSong().getGenre(),
                        h.getSong().getDuration(),
                        h.getSong().getAudioPath(),
                        h.getSong().getCoverImage(),
                        h.getSong().getArtist().getName()
                ))
                .toList();
    }
    
    @Override
    public List<SongDTO> getMyHistory(int page, int size) {

        User user = getCurrentUser();

        if (page < 0 || size <= 0) {
            throw new BadRequestException("Invalid pagination values");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ListeningHistory> historyPage =
                historyRepository.findByListenerOrderByPlayedAtDesc( user,  pageable);

        return historyPage.getContent()
                .stream()
                .map(h -> mapSongToDTO(h.getSong()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void clearMyHistory() {

        User user = getCurrentUser();

        long count = historyRepository.countByListener(user);

        if (count == 0) {
            throw new BadRequestException("History already empty");
        }

        historyRepository.deleteByListener(user);
    }
    private SongDTO mapSongToDTO(Song song) {
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