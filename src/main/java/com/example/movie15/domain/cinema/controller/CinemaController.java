package com.example.movie15.domain.cinema.controller;

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

import com.example.movie15.domain.cinema.dto.CinemaRequestDto;
import com.example.movie15.domain.cinema.dto.CinemaResponseDto;
import com.example.movie15.domain.cinema.service.CinemaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {
	final CinemaService cinemaService;

	//영화관 생성
	@PostMapping
	public ResponseEntity<CinemaResponseDto> createCinema(@RequestBody CinemaRequestDto requestDto) {
		CinemaResponseDto cinema = cinemaService.createCinema(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cinema);
	}
	//영화관에 관람관 추가
	@PostMapping("/{cinemaId}")
	public ResponseEntity<CinemaResponseDto> addHallsToCinema(@PathVariable Long cinemaId,
														      @RequestBody List<Long> hallIds) {
		CinemaResponseDto updatedSinema = cinemaService.addHallsToCinema(cinemaId,hallIds);
		return ResponseEntity.status(HttpStatus.OK).body(updatedSinema);
	}

	//영화관에 속한 관람관 삭제
	@DeleteMapping("/{cinemaId}/halls/{hallId}")
	public ResponseEntity<String> deleteHallFromCinema(@PathVariable Long cinemaId,
		 													      @PathVariable Long hallId) {
		cinemaService.deleteHallFromCinema(cinemaId,hallId);
		return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다.");
	}

	//지역별 영화관 검색
	@GetMapping
	public ResponseEntity<List<String>> findByLocationWithCinemas(@RequestParam String location) {
		List<String> cinemas = cinemaService.findByLocationWithCinemas(location);
		return ResponseEntity.status(HttpStatus.OK).body(cinemas);
	}

	//영화관 상세조회
	@GetMapping("/{cinemaId}")
	public ResponseEntity<CinemaResponseDto> findCinema(@PathVariable Long cinemaId) {
		CinemaResponseDto cinemas = cinemaService.findByCinema(cinemaId);
		return ResponseEntity.status(HttpStatus.OK).body(cinemas);
	}



}
