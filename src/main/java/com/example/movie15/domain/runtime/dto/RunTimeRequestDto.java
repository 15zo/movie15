package com.example.movie15.domain.runtime.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunTimeRequestDto {
	private Long cinemaId;     // 영화관 ID
	private Long hallId;       // 상영관 ID
	private Long movieId;      // 영화 ID
	private LocalDate date;    // 상영 날짜
	private LocalTime startTime; // 상영 시작 시간
	private LocalTime endTime;   // 상영 종료 시간
}