package com.example.movie15.domain.cinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.Response;
import com.example.movie15.domain.cinema.dto.CinemaRequestDto;
import com.example.movie15.domain.cinema.dto.CinemaResponseDto;
import com.example.movie15.domain.cinema.service.CinemaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {
	final CinemaService cinemaService;

	@PostMapping
	public ResponseEntity<CinemaResponseDto> createCinema(@RequestBody CinemaRequestDto requestDto) {
		CinemaResponseDto cinema = cinemaService.createCinema(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cinema);
	}

	@PostMapping("/{cinemaId}")
	public ResponseEntity<CinemaResponseDto> addHallsToCinema(@PathVariable Long cinemaId,
														      @RequestBody List<Long> hallIds) {
		CinemaResponseDto updatedSinema = cinemaService.addHallsToCinema(cinemaId,hallIds);
		return ResponseEntity.status(HttpStatus.OK).body(updatedSinema);
	}

}
