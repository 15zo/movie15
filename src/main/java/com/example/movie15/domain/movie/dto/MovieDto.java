package com.example.movie15.domain.movie.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDto {
	private Long id;
	private String title;
	private String overview;
	private String release_date;
	private Integer runtime;
	private List<GenreDto> genres;
	private String poster_path;
	private String trailerUrl;
}
