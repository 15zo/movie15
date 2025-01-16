package com.example.movie15.domain.booking.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.movie15.domain.booking.dto.BookingRequestDto;
import com.example.movie15.domain.booking.dto.BookingResponseDto;
import com.example.movie15.domain.booking.service.BookingService;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.global.security.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	// 예약 생성
	@PostMapping
	public ResponseEntity<BookingResponseDto> createBooking(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody BookingRequestDto requestDto) {
		User user = userDetails.getUser();
		BookingResponseDto responseDto = bookingService.createBooking(user, requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	// 예매 조회
	@GetMapping
	public ResponseEntity<List<BookingResponseDto>> findAllBooking(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		User user = userDetails.getUser();
		Pageable pageable = PageRequest.of(page, size);
		List<BookingResponseDto> responseDtoList = bookingService.findAllBooking(user, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
	}

	// 예매 취소
	@PostMapping("/{bookingId}")
	public ResponseEntity<Void> cancelBooking(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long bookingId) {
		User user = userDetails.getUser();
		bookingService.cancelBooking(user, bookingId);

		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
}
