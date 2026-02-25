package com.example.demo.service;

<<<<<<< HEAD
import com.example.demo.dto.music.SongDTO;
=======
import com.example.demo.dto.music.HistoryDTO;
import com.example.demo.dto.music.ListeningTimeDTO;
import com.example.demo.dto.music.MostPlayedDTO;

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import java.util.List;

public interface HistoryService {

<<<<<<< HEAD
    void recordPlay(Long songId);

    List<SongDTO> getMyHistory();

    List<SongDTO> getRecentlyPlayed();
=======
    List<HistoryDTO> getRecentHistory();

    List<HistoryDTO> getFullHistory();

    void clearHistory();

	ListeningTimeDTO getListeningTime();

	List<MostPlayedDTO> getMostPlayed();
	
	
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}