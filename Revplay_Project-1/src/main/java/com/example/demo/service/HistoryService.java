package com.example.demo.service;

import com.example.demo.dto.music.HistoryDTO;
import com.example.demo.dto.music.ListeningTimeDTO;
import com.example.demo.dto.music.MostPlayedDTO;

import java.util.List;

public interface HistoryService {

    List<HistoryDTO> getRecentHistory();

    List<HistoryDTO> getFullHistory();

    void clearHistory();

	ListeningTimeDTO getListeningTime();

	List<MostPlayedDTO> getMostPlayed();
	
	
}