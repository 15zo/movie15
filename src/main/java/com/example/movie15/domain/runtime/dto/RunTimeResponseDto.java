package com.example.movie15.domain.runtime.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.example.movie15.domain.runtime.entity.RunTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RunTimeResponseDto {
	private Long id;
	private String cinemaName;
	private String hallName;
	private String movieTitle;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private BigDecimal amount;

	public static RunTimeResponseDto toDto(RunTime runTime) {
		// Cinema 이름 가져오기
		String cinemaName = runTime.getCinemaHall().getCinema().getName();

		return new RunTimeResponseDto(
			runTime.getId(),
			cinemaName,
			runTime.getCinemaHall().getHall().getName(),
			runTime.getMovie().getTitle(),
			runTime.getDate(),
			runTime.getStartTime(),
			runTime.getEndTime(),
			runTime.getAmount()
		);
	}
}