package com.example.demo.controller;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.service.ArtistAnalyticsApiService;
import com.example.demo.service.ArtistProfileApiService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArtistHomeController {

	private final ArtistAnalyticsApiService analyticsService;
	private final ArtistProfileApiService profileService;

	public ArtistHomeController(ArtistAnalyticsApiService analyticsService, ArtistProfileApiService profileService) {

		this.analyticsService = analyticsService;
		this.profileService = profileService;
	}

	@GetMapping("/artist/main")
	public String artistMain(HttpSession session, Model model) {

	    String token = (String) session.getAttribute("JWT_TOKEN");

	    ArtistAnalyticsDTO analytics =
	            analyticsService.getArtistAnalytics(token);

	    ListenerInsightsDTO listeners =
	            analyticsService.getListenerInsights(token);

	    ArtistDTO profile =
	            profileService.getMyProfile(token);

	    String name = "Artist";   // default fallback

	    if (profile != null && profile.getArtistName() != null 
	            && !profile.getArtistName().isBlank()) {

	        name = profile.getArtistName();
	    }

	    model.addAttribute("analytics", analytics);
	    model.addAttribute("listeners", listeners);
	    model.addAttribute("artistName", name);

	    return "artist/artist-home";
	}
	
}