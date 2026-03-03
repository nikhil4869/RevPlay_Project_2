package com.example.demo.controller;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.DailyTrendDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.service.ArtistAnalyticsApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import java.util.List;

@Controller
@RequestMapping("/artist")
public class ArtistAnalyticsPageController {

    private final ArtistAnalyticsApiService analyticsService;
    
	public ArtistAnalyticsPageController(ArtistAnalyticsApiService analyticsService) {
		this.analyticsService = analyticsService;
	}

    // 🔹 THIS IS THE INJECTION
	@GetMapping("/analytics")
	public String analyticsPage(HttpSession session, Model model) {

	    String token = (String) session.getAttribute("JWT_TOKEN");

	    ArtistAnalyticsDTO analytics =
	            analyticsService.getArtistAnalytics(token);

	    ListenerInsightsDTO listenerInsights =
	            analyticsService.getListenerInsights(token);

	    List<SongPlayChartDTO> playChart =
	            analyticsService.getPlayChart(token);

	    List<DailyTrendDTO> dailyTrends =
	            analyticsService.getDailyTrends(token);

	    model.addAttribute("analytics", analytics);
	    model.addAttribute("listeners", listenerInsights);
	    model.addAttribute("playChart", playChart);
	    model.addAttribute("dailyTrends", dailyTrends);

	    return "artist/analytics";
	}
}