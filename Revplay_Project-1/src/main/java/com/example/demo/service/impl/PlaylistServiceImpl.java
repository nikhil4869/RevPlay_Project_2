package com.example.demo.service.impl;

<<<<<<< HEAD
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

=======
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

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

<<<<<<< HEAD
        if (!song.isPublic()) {
            throw new BadRequestException("Cannot add private song to playlist");
        }

        if (playlistSongRepository.existsByPlaylistAndSong(playlist, song)) {
=======
        if (playlistSongRepository.findByPlaylistAndSongId(playlist, songId).isPresent()) {
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
            throw new BadRequestException("Song already in playlist");
        }

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(song);
<<<<<<< HEAD

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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
                .orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));

        playlistSongRepository.delete(ps);
    }
<<<<<<< HEAD

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
=======
    
    @Override
    public List<SongDTO> getPlaylistSongs(Long playlistId) {
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
        );
    }
}