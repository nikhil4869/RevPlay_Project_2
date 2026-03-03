package com.example.demo.controller;

import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.service.ArtistProfileApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/artist")
public class ArtistProfileController {

    private final ArtistProfileApiService profileService;

    public ArtistProfileController(ArtistProfileApiService profileService) {
        this.profileService = profileService;
    }

    // VIEW PROFILE
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        ArtistDTO profile = null;

        try {
            profile = profileService.getMyProfile(token);
        } catch (Exception ignored) {}

        if (profile == null) {
            profile = new ArtistDTO();   // VERY IMPORTANT
            model.addAttribute("exists", false);
        } else {
            model.addAttribute("exists", true);
        }

        model.addAttribute("profile", profile);

        return "artist/profile";
    }

    // EDIT PAGE
    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        ArtistDTO profile = profileService.getMyProfile(token);

        model.addAttribute("profile", profile);
        model.addAttribute("artistId", profile.getId());

        return "artist/profile-edit";
    }

    // CREATE PROFILE
    @PostMapping("/profile/save")
    public String createProfile(@ModelAttribute ArtistDTO dto,
                                HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        profileService.createProfile(token, dto);

        return "redirect:/artist/profile";
    }

    // UPDATE PROFILE
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ArtistDTO dto,
                                @RequestParam(required = false) MultipartFile profileImageFile,
                                @RequestParam(required = false) MultipartFile bannerImageFile,
                                HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        // Update text fields
        profileService.updateProfile(token, dto);

        // Upload profile image if selected
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            profileService.uploadProfileImage(token, dto.getId(), profileImageFile);
        }

        // Upload banner if selected
        if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
            profileService.uploadBannerImage(token, dto.getId(), bannerImageFile);
        }

        return "redirect:/artist/profile";
    }
    // UPLOAD PROFILE IMAGE
    @PostMapping("/profile/{id}/image")
    public String uploadProfileImage(@PathVariable Long id,
                                     @RequestParam MultipartFile image,
                                     HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        profileService.uploadProfileImage(token, id, image);

        return "redirect:/artist/profile";
    }

    // UPLOAD BANNER IMAGE
    @PostMapping("/profile/{id}/banner")
    public String uploadBanner(@PathVariable Long id,
                               @RequestParam MultipartFile image,
                               HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        profileService.uploadBannerImage(token, id, image);

        return "redirect:/artist/profile";
    }
    
    @PostMapping("/profile/deactivate")
    public String deactivateAccount(HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        profileService.deactivateAccount(token);

        session.invalidate(); // logout immediately

        return "redirect:/login";
    }
}