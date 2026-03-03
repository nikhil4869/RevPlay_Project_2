package com.example.demo.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.SongDTO;

@Service
public class ArtistApiService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String BASE_URL = "http://localhost:8080";

	private HttpHeaders getHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return headers;
	}

	/*
	 * ========================== ALBUM METHODS ==========================
	 */

	public List<AlbumDTO> getMyAlbums(String token) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		ResponseEntity<AlbumDTO[]> response = restTemplate.exchange(BASE_URL + "/albums/my", HttpMethod.GET, entity,
				AlbumDTO[].class);

		return Arrays.asList(response.getBody());
	}

	public void createAlbum(String token, String name, String description, String releaseDate) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = "name=" + name + "&description=" + description + "&releaseDate=" + releaseDate;

		HttpEntity<String> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/albums", HttpMethod.POST, entity, String.class);
	}

	public void uploadAlbumCover(String token, Long albumId, MultipartFile image) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("image", image.getResource());

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/albums/" + albumId + "/cover", HttpMethod.POST, entity, String.class);
	}

	public void deleteAlbum(String token, Long albumId) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		restTemplate.exchange(BASE_URL + "/albums/" + albumId, HttpMethod.DELETE, entity, String.class);
	}

	public AlbumDTO getAlbumById(String token, Long id) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		ResponseEntity<AlbumDTO> response = restTemplate.exchange(BASE_URL + "/albums/" + id, HttpMethod.GET, entity,
				AlbumDTO.class);

		return response.getBody();
	}

	public void updateAlbum(String token, Long id, String name, String description, String releaseDate) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = "name=" + name + "&description=" + description + "&releaseDate=" + releaseDate;

		HttpEntity<String> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/albums/" + id, HttpMethod.PUT, entity, String.class);
	}

	/*
	 * ========================== SONG METHODS ==========================
	 */

	public List<SongDTO> getSongsByAlbum(String token, Long albumId) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		ResponseEntity<SongDTO[]> response = restTemplate.exchange(BASE_URL + "/songs/album/" + albumId, HttpMethod.GET,
				entity, SongDTO[].class);

		return Arrays.asList(response.getBody());
	}

	public void removeSongFromAlbum(String token, Long songId) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		restTemplate.exchange(BASE_URL + "/songs/" + songId + "/remove-album", HttpMethod.PUT, entity, String.class);
	}

	public List<SongDTO> getMySongs(String token) {

		HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

		ResponseEntity<SongDTO[]> response = restTemplate.exchange(BASE_URL + "/songs/my", HttpMethod.GET, entity,
				SongDTO[].class);

		return Arrays.asList(response.getBody());
	}

	public void assignSongToAlbum(String token, Long songId, Long albumId, Integer trackNumber) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = "trackNumber=" + trackNumber;

		HttpEntity<String> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/songs/" + songId + "/album/" + albumId, HttpMethod.PUT, entity,
				String.class);
	}

	/*
	 * ========================== NEW: UPLOAD SONG DIRECTLY TO ALBUM
	 * ==========================
	 */

	public void uploadSongToAlbum(String token, String title, String genre, String duration, Integer releaseYear,
			MultipartFile file, Long albumId) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		body.add("title", title);
		body.add("genre", genre);
		body.add("duration", duration);
		body.add("releaseYear", releaseYear);
		body.add("albumId", albumId);
		body.add("file", file.getResource());

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/songs/upload", HttpMethod.POST, entity, String.class);
	}

	public void deleteSong(Long songId, String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		restTemplate.exchange("http://localhost:8080/songs/" + songId, HttpMethod.DELETE, entity, Void.class);
	}

	public void uploadCover(Long songId, MultipartFile image, String token) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("image", image.getResource());

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/songs/" + songId + "/cover", HttpMethod.POST, entity, String.class);
	}

	public void uploadSong(String token, String title, String genre, String duration, Integer releaseYear,
			MultipartFile file) {

		HttpHeaders headers = getHeaders(token);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		body.add("title", title);
		body.add("genre", genre);
		body.add("duration", duration);
		body.add("releaseYear", releaseYear);
		body.add("file", file.getResource());

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

		restTemplate.exchange(BASE_URL + "/songs/upload", HttpMethod.POST, entity, String.class);
	}

}