package com.example.movie15.domain.movie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponseDto {
	private Long id; // 영화 고유 식별자 (DB 내부 ID)
	private String title; // 영화 제목
	private String productionYear; // 제작 연도
	private String category; // 장르
	private String posterUrl; // 영화 포스터 URL
	private Integer duration; // 상영 시간 (분)

	public MovieResponseDto(Long id, String title, String productionYear, String category,
		 String posterUrl, Integer duration) {
		this.id = id;
		this.title = title;
		this.productionYear = productionYear;
		this.category = category;
		this.posterUrl = posterUrl;
		this.duration = duration;
	}
}