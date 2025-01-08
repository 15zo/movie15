package com.example.movie15.domain.movie.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieListResponseDto {
	private int page;
	private List<MovieDto> results;
	private int total_pages;
	private int total_results;
}