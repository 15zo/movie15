package com.example.movie15.domain.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie15.domain.booking.dto.BookingRequestDto;
import com.example.movie15.domain.booking.dto.BookingResponseDto;
import com.example.movie15.domain.booking.service.BookingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	// 예약 생성
	@PostMapping
	public ResponseEntity<BookingResponseDto> createBooking(@RequestParam BookingRequestDto requestDto) {

		BookingResponseDto responseDto = bookingService.createBooking(requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
}
