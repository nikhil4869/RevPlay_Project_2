package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FavoriteService;
import com.example.demo.util.SecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger logger = LogManager.getLogger(FavoriteServiceImpl.class);

    private final FavoriteRepository favoriteRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               SongRepository songRepository,
                               UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;

        logger.info("FavoriteServiceImpl initialized");
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

    @Override
    public void addFavorite(Long songId) {

        logger.info("Adding favorite songId={}", songId);

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (favoriteRepository.findByUserAndSong(user, song).isPresent()) {

            logger.warn("Song already exists in favorites userId={} songId={}",
                    user.getId(), songId);

            throw new BadRequestException("Song already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setSong(song);

        favoriteRepository.save(favorite);

        logger.info("Song added to favorites successfully userId={} songId={}",
                user.getId(), songId);
    }

    @Override
    public void removeFavorite(Long songId) {

        logger.info("Removing favorite songId={}", songId);

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        Favorite favorite = favoriteRepository.findByUserAndSong(user, song)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);

        logger.info("Favorite removed successfully userId={} songId={}",
                user.getId(), songId);
    }

    @Override
    public List<SongDTO> getMyFavorites() {

        logger.info("Fetching favorite songs for current user");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SongDTO> favorites = favoriteRepository.findByUser(user)
                .stream()
                .map(fav -> mapToDTO(fav.getSong()))
                .collect(Collectors.toList());

        logger.info("Total favorites found={} for userId={}", favorites.size(), user.getId());

        return favorites;
    }
}