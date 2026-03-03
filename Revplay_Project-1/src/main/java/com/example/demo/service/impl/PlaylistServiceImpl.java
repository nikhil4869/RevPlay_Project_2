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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final FollowedPlaylistRepository followedPlaylistRepository;
    
    private static final Logger logger = LogManager.getLogger(PlaylistServiceImpl.class);

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
	    logger.debug("User {} attempting to create playlist: {}", email, name);

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.warn("User not found while creating playlist. Email: {}", email);
	                return new ResourceNotFoundException("User not found");
	            });

	    Playlist p = new Playlist();
	    p.setName(name);
	    p.setDescription(description);
	    p.setPublic(isPublic);
	    p.setUser(user);

	    Playlist saved = playlistRepository.save(p);

	    logger.info("Playlist '{}' created successfully by user {}", name, email);

	    return map(saved);
	}

	@Override
	public List<PlaylistDTO> getMyPlaylists() {

	    String email = SecurityUtil.getCurrentUserEmail();
	    logger.debug("Fetching playlists for user {}", email);

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                logger.warn("User not found while fetching playlists. Email={}", email);
	                return new ResourceNotFoundException("User not found");
	            });

	    List<PlaylistDTO> playlists = playlistRepository.findByUser(user)
	            .stream().map(this::map).collect(Collectors.toList());

	    logger.info("User {} has {} playlists", email, playlists.size());

	    return playlists;
	}

	@Override
	public List<PlaylistDTO> getPublicPlaylists() {

	    logger.debug("Fetching all public playlists");

	    List<PlaylistDTO> playlists = playlistRepository.findByIsPublicTrue()
	            .stream()
	            .map(this::map)
	            .collect(Collectors.toList());

	    logger.info("Fetched {} public playlists", playlists.size());

	    return playlists;
	}
    @Override
    public void deletePlaylist(Long id) {

        logger.debug("Delete playlist request. PlaylistId={}", id);

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found for deletion. PlaylistId={}", id);
                    return new ResourceNotFoundException("Playlist not found");
                });

        String playlistName = playlist.getName();

        playlist.setSongs(null);
        playlistRepository.delete(playlist);

        logger.info("Playlist deleted successfully. PlaylistId={}, Name='{}'",
                id, playlistName);
    }
    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Add song request. User={}, PlaylistId={}, SongId={}",
                email, playlistId, songId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found. PlaylistId={}", playlistId);
                    return new ResourceNotFoundException("Playlist not found");
                });

        if (!playlist.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized playlist modification attempt. User={}, PlaylistId={}",
                    email, playlistId);
            throw new UnauthorizedException("Not your playlist");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found. SongId={}", songId);
                    return new ResourceNotFoundException("Song not found");
                });

        if (playlistSongRepository.findByPlaylistAndSongId(playlist, songId).isPresent()) {
            logger.warn("Song already in playlist. PlaylistId={}, SongId={}",
                    playlistId, songId);
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

        logger.info("Song '{}' added to playlist '{}' by user {}",
                song.getTitle(), playlist.getName(), email);
    }
    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Remove song request. User={}, PlaylistId={}, SongId={}",
                email, playlistId, songId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found. PlaylistId={}", playlistId);
                    return new ResourceNotFoundException("Playlist not found");
                });

        if (!playlist.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized removal attempt. User={}, PlaylistId={}",
                    email, playlistId);
            throw new UnauthorizedException("Not your playlist");
        }

        PlaylistSong ps = playlistSongRepository
                .findByPlaylistAndSongId(playlist, songId)
                .orElseThrow(() -> {
                    logger.warn("Song not found in playlist. PlaylistId={}, SongId={}",
                            playlistId, songId);
                    return new ResourceNotFoundException("Song not in playlist");
                });

        playlistSongRepository.delete(ps);

        logger.info("Song removed from playlist '{}' by user {}",
                playlist.getName(), email);
    }
    
    @Override
    public List<SongDTO> getPlaylistSongs(Long playlistId) {

        logger.debug("Fetching songs for playlist id={}", playlistId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found while fetching songs. id={}", playlistId);
                    return new ResourceNotFoundException("Playlist not found");
                });

        logger.debug("Playlist found: name='{}', owner='{}'",
                playlist.getName(),
                playlist.getUser().getEmail());

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
                        ps.getSong().getArtist().getName()
                ))
                .toList();

        logger.info("Fetched {} songs from playlist id={}, name='{}'",
                songs.size(),
                playlistId,
                playlist.getName());

        return songs;
    }
    @Override
    public void followPlaylist(Long playlistId) {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Follow playlist request. User={}, PlaylistId={}",
                email, playlistId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found. Email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found. PlaylistId={}", playlistId);
                    return new ResourceNotFoundException("Playlist not found");
                });

        if (!playlist.isPublic()) {
            logger.warn("Attempt to follow private playlist. User={}, PlaylistId={}",
                    email, playlistId);
            throw new BadRequestException("Cannot follow private playlist");
        }

        if (playlist.getUser().getId().equals(user.getId())) {
            logger.warn("User tried to follow own playlist. User={}", email);
            throw new BadRequestException("You cannot follow your own playlist");
        }

        FollowedPlaylist follow = new FollowedPlaylist();
        follow.setUser(user);
        follow.setPlaylist(playlist);

        followedPlaylistRepository.save(follow);

        logger.info("User {} followed playlist '{}'",
                email, playlist.getName());
    }
    
    @Override
    public void unfollowPlaylist(Long playlistId) {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Unfollow playlist request. User={}, PlaylistId={}",
                email, playlistId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found. Email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist not found. PlaylistId={}", playlistId);
                    return new ResourceNotFoundException("Playlist not found");
                });

        FollowedPlaylist follow = followedPlaylistRepository
                .findByUserAndPlaylist(user, playlist)
                .orElseThrow(() -> {
                    logger.warn("User not following playlist. User={}, PlaylistId={}",
                            email, playlistId);
                    return new ResourceNotFoundException("Not following");
                });

        followedPlaylistRepository.delete(follow);

        logger.info("User {} unfollowed playlist '{}'",
                email, playlist.getName());
    }
    
    @Override
    public List<PlaylistDTO> getFollowedPlaylists() {

        String email = SecurityUtil.getCurrentUserEmail();
        logger.debug("Fetching followed playlists for user {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while fetching followed playlists. Email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        List<FollowedPlaylist> follows = followedPlaylistRepository.findByUser(user);

        logger.debug("User {} follows {} playlists", email, follows.size());

        List<PlaylistDTO> playlists = follows.stream()
                .map(f -> mapToDTO(f.getPlaylist()))
                .collect(Collectors.toList());

        logger.info("Followed playlists fetched successfully for user {}", email);

        return playlists;
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