package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.dto.analytics.DailyTrendDTO;

public interface AnalyticsService {

    ArtistAnalyticsDTO getArtistAnalytics();
    
    List<SongPlayChartDTO> getPlayChart();
    
    ListenerInsightsDTO getListenerInsights();
    

    List<DailyTrendDTO> getDailyTrends();
}
