package com.example.movie15.domain.movie.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoResponse {
	private List<VideoDto> results;
}
