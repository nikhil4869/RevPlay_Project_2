package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserPlaylistController {

    private final UserApiService userApiService;

    public UserPlaylistController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/playlists")
    public String showPlaylists(@RequestParam(defaultValue = "my") String type, HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        // Check for error from session (e.g. from createPlaylist)
        String sessionError = (String) session.getAttribute("playlistError");
        if (sessionError != null) {
            model.addAttribute("errorMessage", sessionError);
            session.removeAttribute("playlistError");
        }

        try {
            List<PlaylistDTO> playlists;
            String title = "Playlists";
            if ("public".equals(type)) {
                playlists = userApiService.getPublicPlaylists(token);
                title = "Public Playlists";
            } else if ("followed".equals(type)) {
                playlists = userApiService.getFollowedPlaylists(token);
                title = "Followed Playlists";
            } else {
                playlists = userApiService.getMyPlaylists(token);
                title = "My Playlists";
            }
            
            model.addAttribute("playlists", playlists);
            model.addAttribute("type", type);
            model.addAttribute("pageTitle", title);
            return "user/playlists";
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized | org.springframework.web.client.HttpClientErrorException.Forbidden ex) {
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching playlists: " + e.getMessage());
            return "user/playlists";
        }
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(@RequestParam String name, @RequestParam String description, 
                               @RequestParam(defaultValue = "false") boolean isPublic, 
                               HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null || token.isEmpty()) return "redirect:/login";

        try {
            userApiService.createPlaylist(name, description, isPublic, token);
            return "redirect:/user/playlists";
        } catch (Exception e) {
            // Use redirect to clear state and reload data, using session if we want to pass errors
            session.setAttribute("playlistError", "Error creating playlist: " + cleanMessage(e.getMessage()));
            return "redirect:/user/playlists";
        }
    }

    @PostMapping("/playlists/{id}/delete")
    public String deletePlaylist(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token != null) {
            try {
                userApiService.deletePlaylist(id, token);
            } catch (Exception e) {
                session.setAttribute("playlistError", "Could not delete playlist: " + cleanMessage(e.getMessage()));
            }
        }
        return "redirect:/user/playlists";
    }

    @PostMapping("/playlists/{id}/follow")
    public String followPlaylist(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token != null) {
            try {
                userApiService.followPlaylist(id, token);
            } catch (Exception e) {
                session.setAttribute("playlistError", "Could not follow playlist: " + cleanMessage(e.getMessage()));
            }
        }
        return "redirect:/user/playlists?type=public";
    }

    @PostMapping("/playlists/{id}/unfollow")
    public String unfollowPlaylist(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token != null) {
            try {
                userApiService.unfollowPlaylist(id, token);
            } catch (Exception e) {
                session.setAttribute("playlistError", "Could not unfollow playlist: " + cleanMessage(e.getMessage()));
            }
        }
        return "redirect:/user/playlists?type=followed";
    }

    @GetMapping("/playlists/{id}/songs")
    public String showPlaylistSongs(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<SongDTO> songs = userApiService.getPlaylistSongs(id, token);
            model.addAttribute("songs", songs);
            model.addAttribute("playlistId", id);
            model.addAttribute("pageTitle", "Playlist Songs");
            model.addAttribute("isAddView", false); // explicit
            return "user/playlist-songs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching playlist songs: " + e.getMessage());
            // Instead of returning user/playlists (which lacks data), redirect back to playlists list
            return "redirect:/user/playlists";
        }
    }

    @GetMapping("/playlists/{id}/add-songs")
    public String showAddSongs(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null || token.isEmpty()) return "redirect:/login";

        try {
            List<SongDTO> allSongs = userApiService.getAllSongs(token);
            model.addAttribute("songs", allSongs);
            model.addAttribute("playlistId", id);
            model.addAttribute("isAddView", true);
            model.addAttribute("pageTitle", "Add Songs to Playlist");
            return "user/playlist-songs";
        } catch (Exception e) {
            return "redirect:/user/playlists";
        }
    }

    @PostMapping("/playlists/{playlistId}/songs/{songId}/remove")
    public String removeSong(@PathVariable Long playlistId, @PathVariable Long songId, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token != null) {
            try {
                userApiService.removeSongFromPlaylist(playlistId, songId, token);
            } catch (Exception e) {
                session.setAttribute("playlistError", "Could not remove song: " + cleanMessage(e.getMessage()));
            }
        }
        return "redirect:/user/playlists/" + playlistId + "/songs";
    }

    private String cleanMessage(String rawMessage) {
        if (rawMessage == null) return "Unknown error";
        
        // If it's a JSON response from backend
        if (rawMessage.contains("\"message\"")) {
            try {
                int start = rawMessage.indexOf("\"message\":\"") + 11;
                int end = rawMessage.indexOf("\"", start);
                if (start > 10 && end > start) {
                    return rawMessage.substring(start, end);
                }
            } catch (Exception e) {
                // fallback
            }
        }
        
        // If it contains the RestTemplate error prefix, strip it
        if (rawMessage.contains(" : ")) {
            return rawMessage.substring(rawMessage.lastIndexOf(" : ") + 3).replace("\"", "");
        }
        
        return rawMessage.replace("\"", "");
    }
}
