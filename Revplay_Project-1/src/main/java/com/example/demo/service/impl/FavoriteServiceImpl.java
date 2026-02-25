package com.example.demo.service.impl;

<<<<<<< HEAD
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BadRequestException;
=======
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FavoriteService;
import com.example.demo.util.SecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               SongRepository songRepository,
                               UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

<<<<<<< HEAD
    @Override
    public void addToFavorites(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
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

    @Override
    public void addFavorite(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

<<<<<<< HEAD
        favoriteRepository.findByListenerAndSong(listener, song)
                .ifPresent(f -> {
                    throw new BadRequestException("Already in favorites");
                });

        Favorite favorite = new Favorite();
        favorite.setListener(listener);
=======
        if (favoriteRepository.findByUserAndSong(user, song).isPresent()) {
            throw new BadRequestException("Song already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
        favorite.setSong(song);

        favoriteRepository.save(favorite);
    }

    @Override
<<<<<<< HEAD
    public void removeFromFavorites(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
=======
    public void removeFavorite(Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

<<<<<<< HEAD
        Favorite favorite = favoriteRepository
                .findByListenerAndSong(listener, song)
=======
        Favorite favorite = favoriteRepository.findByUserAndSong(user, song)
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }

    @Override
<<<<<<< HEAD
    public List<Song> getMyFavorites() {

        String email = SecurityUtil.getCurrentUserEmail();

        User listener = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favoriteRepository.findByListener(listener)
                .stream()
                .map(Favorite::getSong)
                .collect(Collectors.toList());
    }
}
=======
    public List<SongDTO> getMyFavorites() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favoriteRepository.findByUser(user)
                .stream()
                .map(fav -> mapToDTO(fav.getSong()))
                .collect(Collectors.toList());
    }
}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
