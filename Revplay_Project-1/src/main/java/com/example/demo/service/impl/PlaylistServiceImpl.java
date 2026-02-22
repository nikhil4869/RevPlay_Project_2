package com.example.demo.service.impl;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.*;
import com.example.demo.service.PlaylistService;
import com.example.demo.util.SecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                               PlaylistSongRepository playlistSongRepository,
                               SongRepository songRepository,
                               UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    // ================= CREATE PLAYLIST =================

    @Override
    public PlaylistDTO createPlaylist(String name) {

        User listener = getCurrentUser();

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setListener(listener);
        playlist.setPublic(false); // default private

        return mapBasicDTO(playlistRepository.save(playlist));
    }

    // ================= ADD SONG =================

    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {

        Playlist playlist = getOwnedPlaylist(playlistId);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (!song.isPublic()) {
            throw new BadRequestException("Cannot add private song to playlist");
        }

        if (playlistSongRepository.existsByPlaylistAndSong(playlist, song)) {
            throw new BadRequestException("Song already in playlist");
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);

        playlistSongRepository.save(ps);
    }

    // ================= REMOVE SONG =================

    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {

        Playlist playlist = getOwnedPlaylist(playlistId);

        PlaylistSong ps = playlistSongRepository.findByPlaylist(playlist)
                .stream()
                .filter(p -> p.getSong().getId().equals(songId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));

        playlistSongRepository.delete(ps);
    }

    // ================= MY PLAYLISTS =================

    @Override
    public List<PlaylistDTO> getMyPlaylists() {

        User listener = getCurrentUser();

        return playlistRepository.findByListener(listener)
                .stream()
                .map(this::mapBasicDTO)
                .collect(Collectors.toList());
    }

    // ================= PLAYLIST WITH SONGS =================

    @Override
    public PlaylistDTO getPlaylistWithSongs(Long playlistId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        User currentUser = getCurrentUser();

        // 🔐 Security Check
        if (!playlist.isPublic() &&
            !playlist.getListener().getId().equals(currentUser.getId())) {

            throw new UnauthorizedException("You cannot access this private playlist");
        }

        List<PlaylistSong> playlistSongs =
                playlistSongRepository.findByPlaylist(playlist);

        PlaylistDTO dto = mapBasicDTO(playlist);

        dto.setSongs(
                playlistSongs.stream()
                        .map(ps -> mapSong(ps.getSong()))
                        .collect(Collectors.toList())
        );

        dto.setTotalSongs(playlistSongs.size());

        return dto;
    }

    // ================= CHANGE VISIBILITY =================

    @Override
    public PlaylistDTO changeVisibility(Long playlistId, boolean isPublic) {

        Playlist playlist = getOwnedPlaylist(playlistId);

        playlist.setPublic(isPublic);

        return mapBasicDTO(playlistRepository.save(playlist));
    }

    // ================= PUBLIC PLAYLISTS =================

    @Override
    public List<PlaylistDTO> getPublicPlaylists() {

        return playlistRepository.findByIsPublicTrue()
                .stream()
                .map(this::mapBasicDTO)
                .collect(Collectors.toList());
    }

    // ================= HELPER METHODS =================

    private User getCurrentUser() {

        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Playlist getOwnedPlaylist(Long playlistId) {

        User listener = getCurrentUser();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getListener().getId().equals(listener.getId())) {
            throw new UnauthorizedException("Unauthorized access");
        }

        return playlist;
    }

    private PlaylistDTO mapBasicDTO(Playlist playlist) {

        long count = playlistSongRepository.countByPlaylist(playlist);

        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setPublic(playlist.isPublic());
        dto.setTotalSongs((int) count);

        return dto;
    }

    private SongDTO mapSong(Song song) {

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