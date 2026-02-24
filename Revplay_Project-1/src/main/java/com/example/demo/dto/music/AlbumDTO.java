package com.example.demo.dto.music;

import java.time.LocalDate;

public class AlbumDTO {

	private Long id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private String coverImage;
	private String artistName;

	public AlbumDTO() {
	}

	public AlbumDTO(Long id, String name, String description, LocalDate releaseDate, String coverImage,
			String artistName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
		this.coverImage = coverImage;
		this.artistName = artistName;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public String getArtistName() {
		return artistName;
	}
}
