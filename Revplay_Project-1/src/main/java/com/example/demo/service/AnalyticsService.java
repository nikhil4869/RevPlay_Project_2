package com.example.demo.service;

<<<<<<< HEAD
import java.util.Map;

import com.example.demo.dto.analytics.UserAnalyticsDTO;

import java.util.List;

public interface AnalyticsService {
	
	 UserAnalyticsDTO getMyAnalytics();

    Map<String, Object> getListenerSummary();

    List<?> getTopSongs(int limit);

    List<?> getTopGenres();
}
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
