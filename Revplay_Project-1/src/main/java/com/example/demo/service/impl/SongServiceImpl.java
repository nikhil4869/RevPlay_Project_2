package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Song;
import com.example.demo.repository.SongRepository;
import com.example.demo.service.SongService;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;

    @Override
    public List<SongDTO> getAllSongs() {

        List<Song> songs = songRepository.findAll();

        return songs.stream().map(song -> {

            SongDTO dto = new SongDTO();   

            dto.setId(song.getId());
            dto.setTitle(song.getTitle());
            dto.setDuration(song.getDuration());

            return dto;

        }).toList();
    }
    
}