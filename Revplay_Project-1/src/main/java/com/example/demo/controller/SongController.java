package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.SongService;

@RestController
@RequestMapping("/api")
public class SongController {
	
	 @Autowired
	 private SongService songService;
	 
	  @GetMapping("/songs")
	    public List<SongDTO> getAllSongs() {
	        return songService.getAllSongs();
	    }

}