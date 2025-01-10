package com.example.movie15.domain.runtime.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.domain.cinema.repository.HallRepository;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;
import com.example.movie15.domain.runtime.dto.RunTimeRequestDto;
import com.example.movie15.domain.runtime.dto.RunTimeResponseDto;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.repository.RunTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunTimeService {
	private final RunTimeRepository runTimeRepository;
	private final MovieRepository movieRepository;
	private final HallRepository hallRepository;

@Transactional
	public RunTimeResponseDto createRunTime(RunTimeRequestDto requestDto) {
		Hall hall = hallRepository.findById(requestDto.getHallId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상영관 ID입니다."));
		Movie movie = movieRepository.findById(requestDto.getMovieId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 영화 ID입니다."));

		RunTime runTime = new RunTime(
			hall,
			movie,
			requestDto.getDate(),
			requestDto.getStartTime(),
			requestDto.getEndTime()
		);

		RunTime savedRunTime = runTimeRepository.save(runTime);
		return RunTimeResponseDto.toDto(savedRunTime);
	}
}
