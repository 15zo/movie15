package com.example.movie15.domain.runtime.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieScheduleDto {
	private Long runtimeId;
	private String hallName;      // 상영관 이름
	private LocalTime startTime;  // 시작 시간
	private LocalTime endTime;    // 종료 시간
}
