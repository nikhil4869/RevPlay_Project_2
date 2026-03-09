package com.example.demo.service.impl;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.entity.Playlist;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PlaylistService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Song;
import com.example.demo.repository.PlaylistSongRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.FollowedPlaylist;
import com.example.demo.repository.FollowedPlaylistRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LogManager.getLogger(PlaylistServiceImpl.class);

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final FollowedPlaylistRepository followedPlaylistRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, UserRepository userRepository,
            PlaylistSongRepository playlistSongRepository, SongRepository songRepository,
            FollowedPlaylistRepository followedPlaylistRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.songRepository = songRepository;
        this.followedPlaylistRepository = followedPlaylistRepository;

        logger.info("PlaylistServiceImpl initialized");
    }

    private PlaylistDTO convertToDTO(Playlist p, Boolean followedStatus) {

        logger.debug("Converting playlist to DTO playlistId={}", p != null ? p.getId() : null);

        if (p == null) return null;

        String name = p.getName();
        String description = p.getDescription();

        try {

            if (name != null && name.contains("%")) {
                name = URLDecoder.decode(name, StandardCharsets.UTF_8);
            }

            if (description != null && description.contains("%")) {
                description = URLDecoder.decode(description, StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            logger.warn("Failed to decode playlist name or description playlistId={}", p.getId());
        }

        String ownerName = (p.getUser() != null) ? p.getUser().getName() : "Unknown";

        return new PlaylistDTO(
                p.getId(),
                name,
                description,
                p.isPublic(),
                ownerName,
                followedStatus
        );
    }

    private PlaylistDTO convertToDTO(Playlist p) {
        return convertToDTO(p, null);
    }

    @Override
    public PlaylistDTO createPlaylist(String name, String description, boolean isPublic) {

        logger.info("Creating playlist name={}", name);

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist p = new Playlist();
        p.setName(name);
        p.setDescription(description);
        p.setPublic(isPublic);
        p.setUser(user);

        Playlist saved = playlistRepository.save(p);

        logger.info("Playlist created playlistId={}", saved.getId());

        return convertToDTO(saved);
    }

    @Override
    public List<PlaylistDTO> getMyPlaylists() {

        logger.info("Fetching user playlists");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PlaylistDTO> playlists = playlistRepository.findByUser(user)
                .stream()
                .map(p -> convertToDTO(p, false))
                .collect(Collectors.toList());

        logger.info("User playlists fetched count={}", playlists.size());

        return playlists;
    }

    @Override
    public List<PlaylistDTO> getPublicPlaylists() {

        logger.info("Fetching public playlists");

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email).orElse(null);

        List<Long> followedPlaylistIds = new java.util.ArrayList<>();

        if (currentUser != null) {
            followedPlaylistIds = followedPlaylistRepository.findByUser(currentUser)
                    .stream()
                    .map(f -> f.getPlaylist().getId())
                    .collect(Collectors.toList());
        }

        final List<Long> finalFollowedIds = followedPlaylistIds;

        List<PlaylistDTO> playlists = playlistRepository.findByIsPublicTrue()
                .stream()
                .filter(p -> currentUser == null || !p.getUser().getId().equals(currentUser.getId()))
                .map(p -> convertToDTO(p, finalFollowedIds.contains(p.getId())))
                .collect(Collectors.toList());

        logger.info("Public playlists fetched count={}", playlists.size());

        return playlists;
    }

    @Override
    public void deletePlaylist(Long id) {

        logger.info("Deleting playlist playlistId={}", id);

        Playlist p = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        p.setSongs(null);
        playlistRepository.delete(p);

        logger.info("Playlist deleted playlistId={}", id);
    }

    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {

        logger.info("Adding song to playlist playlistId={} songId={}", playlistId, songId);

        String email = SecurityUtil.getCurrentUserEmail();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized playlist modification attempt playlistId={}", playlistId);
            throw new UnauthorizedException("Not your playlist");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (playlistSongRepository.findByPlaylistAndSongId(playlist, songId).isPresent()) {
            logger.warn("Song already exists in playlist playlistId={} songId={}", playlistId, songId);
            throw new BadRequestException("Song already in playlist");
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);
        ps.setPosition(
                playlistSongRepository
                        .findByPlaylistOrderByPositionAsc(playlist)
                        .size() + 1
        );

        playlistSongRepository.save(ps);

        logger.info("Song added to playlist playlistId={} songId={}", playlistId, songId);
    }

    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {

        logger.info("Removing song from playlist playlistId={} songId={}", playlistId, songId);

        String email = SecurityUtil.getCurrentUserEmail();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized remove attempt playlistId={}", playlistId);
            throw new UnauthorizedException("Not your playlist");
        }

        PlaylistSong ps = playlistSongRepository
                .findByPlaylistAndSongId(playlist, songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));

        playlistSongRepository.delete(ps);

        logger.info("Song removed from playlist playlistId={} songId={}", playlistId, songId);
    }

    @Override
    public List<SongDTO> getPlaylistSongs(Long playlistId) {

        logger.info("Fetching songs from playlist playlistId={}", playlistId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        List<SongDTO> songs = playlistSongRepository
                .findByPlaylistOrderByPositionAsc(playlist)
                .stream()
                .map(ps -> new SongDTO(
                        ps.getSong().getId(),
                        ps.getSong().getTitle(),
                        ps.getSong().getGenre(),
                        ps.getSong().getDuration(),
                        ps.getSong().getAudioPath(),
                        ps.getSong().getCoverImage(),
                        ps.getSong().getArtist().getName(),
                        ps.getSong().getAlbum() != null ? ps.getSong().getAlbum().getName() : null
                ))
                .toList();

        logger.info("Playlist songs fetched count={}", songs.size());

        return songs;
    }

    @Override
    public void followPlaylist(Long playlistId) {

        logger.info("Follow playlist request playlistId={}", playlistId);

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.isPublic()) {
            logger.warn("Attempt to follow private playlist playlistId={}", playlistId);
            throw new BadRequestException("Cannot follow private playlist");
        }

        if (playlist.getUser().getId().equals(user.getId())) {
            logger.warn("User attempted to follow own playlist userId={} playlistId={}", user.getId(), playlistId);
            throw new BadRequestException("You cannot follow your own playlist");
        }

        if (followedPlaylistRepository.findByUserAndPlaylist(user, playlist).isPresent()) {
            logger.warn("Already following playlist userId={} playlistId={}", user.getId(), playlistId);
            throw new BadRequestException("Already following");
        }

        FollowedPlaylist follow = new FollowedPlaylist();
        follow.setUser(user);
        follow.setPlaylist(playlist);

        followedPlaylistRepository.save(follow);

        logger.info("Playlist followed userId={} playlistId={}", user.getId(), playlistId);
    }

    @Override
    public void unfollowPlaylist(Long playlistId) {

        logger.info("Unfollow playlist request playlistId={}", playlistId);

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        FollowedPlaylist follow = followedPlaylistRepository
                .findByUserAndPlaylist(user, playlist)
                .orElseThrow(() -> new ResourceNotFoundException("Not following"));

        followedPlaylistRepository.delete(follow);

        logger.info("Playlist unfollowed userId={} playlistId={}", user.getId(), playlistId);
    }

    @Override
    public List<PlaylistDTO> getFollowedPlaylists() {

        logger.info("Fetching followed playlists");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<FollowedPlaylist> follows = followedPlaylistRepository.findByUser(user);

        List<PlaylistDTO> playlists = follows.stream()
                .map(f -> convertToDTO(f.getPlaylist(), true))
                .collect(Collectors.toList());

        logger.info("Followed playlists fetched count={}", playlists.size());

        return playlists;
    }
}