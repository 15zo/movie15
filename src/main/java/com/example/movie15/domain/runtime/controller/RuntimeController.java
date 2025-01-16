package com.example.movie15.domain.runtime.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie15.domain.runtime.dto.MovieScheduleDto;
import com.example.movie15.domain.runtime.dto.RunTimeRequestDto;
import com.example.movie15.domain.runtime.dto.RunTimeResponseDto;
import com.example.movie15.domain.runtime.dto.SeatDto;
import com.example.movie15.domain.runtime.service.RunTimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/runtimes")
public class RuntimeController {
	private final RunTimeService runTimeService;

	//상영시간 등록
	@PostMapping
	public ResponseEntity<RunTimeResponseDto> createRunTime(@RequestBody RunTimeRequestDto requestDto) {
		RunTimeResponseDto createdRunTime = runTimeService.createRunTime(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRunTime);
	}

	//상영시간 삭제
	@DeleteMapping("{runtimeId}")
	public ResponseEntity<String> deleteRunTIme(@PathVariable Long runtimeId) {
		runTimeService.deleteRunTime(runtimeId);
		return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다.");
	}

	//영화관 영화 상영시간 조회(cinema_hallId 로 바꾸는게 좋을지)
	@GetMapping("/cinemas/{cinemaId}/movies/{movieId}/schedule")
	public ResponseEntity<List<MovieScheduleDto>> getMovieSchedule(
		@PathVariable Long cinemaId,
		@PathVariable Long movieId,
		@RequestParam LocalDate date) {
		List<MovieScheduleDto> schedule = runTimeService.getMovieSchedule(cinemaId, movieId, date);
		return ResponseEntity.status(HttpStatus.OK).body(schedule);
	}

	//해당상영시간 좌석조회
	@GetMapping("/{runtimeId}")
	public ResponseEntity<List<SeatDto>> getSeatsByRunTime(@PathVariable Long runtimeId) {
		List<SeatDto> seatList = runTimeService.getSeatsByRunTime(runtimeId);
		return ResponseEntity.status(HttpStatus.OK).body(seatList);
	}
}
