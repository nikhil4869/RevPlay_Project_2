package com.example.demo.service;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.DailyTrendDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArtistAnalyticsApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public ArtistAnalyticsDTO getArtistAnalytics(String token) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

        ResponseEntity<ArtistAnalyticsDTO> response =
                restTemplate.exchange(
                        BASE_URL + "/analytics/artist",
                        HttpMethod.GET,
                        entity,
                        ArtistAnalyticsDTO.class
                );

        return response.getBody();
    }

    public ListenerInsightsDTO getListenerInsights(String token) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

        ResponseEntity<ListenerInsightsDTO> response =
                restTemplate.exchange(
                        BASE_URL + "/analytics/listeners",
                        HttpMethod.GET,
                        entity,
                        ListenerInsightsDTO.class
                );

        return response.getBody();
    }
    
    public List<SongPlayChartDTO> getPlayChart(String token) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

        ResponseEntity<List<SongPlayChartDTO>> response =
                restTemplate.exchange(
                        BASE_URL + "/analytics/plays-chart",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<SongPlayChartDTO>>() {}
                );

        return response.getBody();
    }
    
    public List<DailyTrendDTO> getDailyTrends(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<DailyTrendDTO[]> response =
                restTemplate.exchange(
                        BASE_URL + "/analytics/trends",
                        HttpMethod.GET,
                        entity,
                        DailyTrendDTO[].class
                );

        return Arrays.asList(response.getBody());
    }
}