package com.example.movie15.domain.movie.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDetailsResponseDto {
	private Long id; // TMDB 영화 ID
	private String title; // 제목 (한국어)
	private String overview; // 줄거리 (한국어)
	private String release_date; // 개봉일
	private Integer runtime; // 상영 시간
	private String status; // 상영 상태
	private String genres; // 장르 리스트 (한국어)
	private String poster_path; // 포스터 경로
	private String trailerUrl; // 예고편 경로
}
