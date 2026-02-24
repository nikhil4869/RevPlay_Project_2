package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.user.DashboardDTO;
import com.example.demo.dto.user.UpdateProfileDTO;
import com.example.demo.dto.user.UserProfileDTO;

import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ListeningHistoryRepository;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlaylistRepository playlistRepository;
    private final ListeningHistoryRepository historyRepository;

    public UserServiceImpl(UserRepository userRepository,
                           SongRepository songRepository,
                           AlbumRepository albumRepository,
                           FavoriteRepository favoriteRepository,
                           PlaylistRepository playlistRepository,
                           ListeningHistoryRepository historyRepository) {

        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.favoriteRepository = favoriteRepository;
        this.playlistRepository = playlistRepository;
        this.historyRepository = historyRepository;
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

   
    // PROFILE
    
    @Override
    public UserProfileDTO getMyProfile() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getRole().getName()
        );
    }

    @Override
    public void updateMyProfile(UpdateProfileDTO dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(dto.getName());
        user.setDateOfBirth(dto.getDateOfBirth());

        userRepository.save(user);
    }

    
    // STATS (Listener Only)


   
    // DEACTIVATE
    
    @Override
    public void deactivateMyAccount() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(false);
        userRepository.save(user);
    }


    // DASHBOARD
   
    @Override
    public DashboardDTO getMyDashboard() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DashboardDTO dto = new DashboardDTO();

        // 1️⃣ Total Songs Liked
        dto.setTotalSongsLiked(
                favoriteRepository.countByListener(user)
        );

        // 2️⃣ Total Playlists
        dto.setTotalPlaylists(
                playlistRepository.countByListener(user)
        );

        // 3️⃣ Total Songs Played
        dto.setTotalSongsPlayed(
                historyRepository.countByListener(user)
        );

        // 4️⃣ Total Listening Time (Safe Calculation)
        long totalMinutes = historyRepository.findByListener(user)
                .stream()
                .map(h -> h.getSong().getDuration()) // format "4:18"
                .mapToLong(duration -> {
                    try {
                        String[] parts = duration.split(":");
                        int minutes = Integer.parseInt(parts[0]);
                        return minutes;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();

        dto.setTotalListeningMinutes(totalMinutes);

        // 5️⃣ Recently Played
        List<SongDTO> recent = historyRepository
                .findTop5ByListenerOrderByPlayedAtDesc(user)
                .stream()
                .map(h -> mapSongToDTO(h.getSong()))
                .toList();

        dto.setRecentlyPlayed(recent);

        // 6️⃣ Most Played Songs (Global Trending)
        List<SongDTO> mostPlayed = songRepository
                .findTop5ByOrderByPlayCountDesc()
                .stream()
                .map(this::mapSongToDTO)
                .toList();

        dto.setMostPlayed(mostPlayed);

        return dto;
    }
}