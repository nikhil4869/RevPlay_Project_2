package com.example.demo.controller;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.service.ArtistAnalyticsApiService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ArtistAnalyticsApiService analyticsService;

    public DashboardController(ArtistAnalyticsApiService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        ArtistAnalyticsDTO analytics =
                analyticsService.getArtistAnalytics(token);

        ListenerInsightsDTO listenerInsights =
                analyticsService.getListenerInsights(token);

        List<SongPlayChartDTO> playChart =
                analyticsService.getPlayChart(token);

        model.addAttribute("analytics", analytics);
        model.addAttribute("listeners", listenerInsights);
        model.addAttribute("playChart", playChart);

        return "dashboard";
    }
}