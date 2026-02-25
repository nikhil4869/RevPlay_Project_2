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
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.entity.PlaylistSong;
import com.example.demo.entity.Song;
import com.example.demo.repository.PlaylistSongRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.FollowedPlaylist;
import com.example.demo.repository.FollowedPlaylistRepository;

@Service
public class PlaylistServiceImpl implements PlaylistService {

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
	}

	private PlaylistDTO map(Playlist p) {
        return new PlaylistDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.isPublic(),
                p.getUser().getName()
        );
    }

    @Override
    public PlaylistDTO createPlaylist(String name, String description, boolean isPublic) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist p = new Playlist();
        p.setName(name);
        p.setDescription(description);
        p.setPublic(isPublic);
        p.setUser(user);

        return map(playlistRepository.save(p));
    }

    @Override
    public List<PlaylistDTO> getMyPlaylists() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return playlistRepository.findByUser(user)
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> getPublicPlaylists() {
        return playlistRepository.findByIsPublicTrue()
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public void deletePlaylist(Long id) {

        Playlist p = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        p.setSongs(null);
        playlistRepository.delete(p);
    }
    
    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Not your playlist");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if (playlistSongRepository.findByPlaylistAndSongId(playlist, songId).isPresent()) {
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
    }
    
    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Not your playlist");
        }

        PlaylistSong ps = playlistSongRepository
                .findByPlaylistAndSongId(playlist, songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));

        playlistSongRepository.delete(ps);
    }
    
    @Override
    public List<SongDTO> getPlaylistSongs(Long playlistId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        return playlistSongRepository
                .findByPlaylistOrderByPositionAsc(playlist)
                .stream()
                .map(ps -> new SongDTO(
                        ps.getSong().getId(),
                        ps.getSong().getTitle(),
                        ps.getSong().getGenre(),
                        ps.getSong().getDuration(),
                        ps.getSong().getAudioPath(),
                        ps.getSong().getCoverImage(),
                        ps.getSong().getArtist().getName()
                ))
                .toList();
    }
    
    @Override
    public void followPlaylist(Long playlistId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!playlist.isPublic()) {
            throw new BadRequestException("Cannot follow private playlist");
        }
        
        // prevent following own playlist
        if (playlist.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot follow your own playlist");
        }

        if (followedPlaylistRepository.findByUserAndPlaylist(user, playlist).isPresent()) {
            throw new BadRequestException("Already following");
        }

        FollowedPlaylist follow = new FollowedPlaylist();
        follow.setUser(user);
        follow.setPlaylist(playlist);

        followedPlaylistRepository.save(follow);
    }
    
    @Override
    public void unfollowPlaylist(Long playlistId) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        FollowedPlaylist follow = followedPlaylistRepository
                .findByUserAndPlaylist(user, playlist)
                .orElseThrow(() -> new ResourceNotFoundException("Not following"));

        followedPlaylistRepository.delete(follow);
    }
    
    @Override
    public List<PlaylistDTO> getFollowedPlaylists() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<FollowedPlaylist> follows = followedPlaylistRepository.findByUser(user);

        return follows.stream()
                .map(f -> mapToDTO(f.getPlaylist()))
                .collect(Collectors.toList());
    }
    
    private PlaylistDTO mapToDTO(Playlist playlist) {
        return new PlaylistDTO(
                playlist.getId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.isPublic(),
                playlist.getUser().getName()
        );
    }
}