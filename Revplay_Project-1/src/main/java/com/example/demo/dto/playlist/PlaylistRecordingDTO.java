package com.example.demo.dto.playlist;

public class PlaylistRecordingDTO {

    private Long id;
    private String title;
    private String filePath;

    public PlaylistRecordingDTO(Long id, String title, String filePath) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
    }

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getFilePath() {
		return filePath;
	}
}
