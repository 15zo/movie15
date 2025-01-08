package com.example.movie15.domain.movie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.movie15.domain.movie.dto.MovieDto;
import com.example.movie15.domain.movie.dto.MovieListResponseDto;
import com.example.movie15.domain.movie.dto.VideoResponse;
import com.example.movie15.global.config.TmdbConfig;

@Service
public class TmdbService {
	private final RestTemplate restTemplate;
	private final TmdbConfig tmdbConfig;
	public TmdbService(@Qualifier("tmdbRestTemplate") RestTemplate restTemplate, TmdbConfig tmdbConfig) {
		this.restTemplate = restTemplate;
		this.tmdbConfig = tmdbConfig;
	}

	// 특정 영화 정보 가져오기 (한국어 데이터)
	public MovieDto getMovieDetails(Long tmdbId) {
		String url = String.format("%s/movie/%d?api_key=%s&language=ko-KR",
			tmdbConfig.getBaseUrl(), tmdbId, tmdbConfig.getApiKey());
		return restTemplate.getForObject(url, MovieDto.class);
	}

	public String getTrailerUrl(Long movieId) {
		String url = String.format("%s/movie/%d/videos?api_key=%s&language=ko-KR",
			tmdbConfig.getBaseUrl(), movieId, tmdbConfig.getApiKey());
		VideoResponse response = restTemplate.getForObject(url, VideoResponse.class);

		if (response != null && response.getResults() != null) {
			return response.getResults().stream()
				.filter(video -> "Trailer".equalsIgnoreCase(video.getType()) && "YouTube".equalsIgnoreCase(video.getSite()))
				.map(video -> "https://www.youtube.com/watch?v=" + video.getKey())
				.findFirst()
				.orElse(null);
		}
		return null;
	}

	public List<MovieDto> getPopularMovies(int page) {
		String url = String.format("%s/movie/popular?api_key=%s&page=%d&language=ko-KR",
			tmdbConfig.getBaseUrl(), tmdbConfig.getApiKey(), page);
		MovieListResponseDto response = restTemplate.getForObject(url, MovieListResponseDto.class);
		return response != null ? response.getResults() : List.of();
	}
}