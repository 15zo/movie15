package com.example.movie15.domain.cinema.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.domain.cinema.entity.Hall;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CinemaResponseDto {
	private Long id;
	private String name;
	private String location;
	private List<String> hallNames;

	public static CinemaResponseDto toDto (Cinema cinema) {
		List<String> hallNames = cinema.getCinemaHalls().stream()
			.map(cinemaHall ->cinemaHall.getHall().getName())
			.collect(Collectors.toList());
		return new CinemaResponseDto(
			cinema.getId(),
			cinema.getName(),
			cinema.getLocation(),
			hallNames
			);
	}
}
