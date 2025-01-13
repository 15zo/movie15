package com.example.movie15.domain.booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.booking.dto.BookingRequestDto;
import com.example.movie15.domain.booking.dto.BookingResponseDto;
import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.entity.BookingSeat;
import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.booking.repository.BookingSeatRepository;
import com.example.movie15.domain.cinema.entity.Seat;
import com.example.movie15.domain.cinema.repository.SeatRepository;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.repository.RunTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

	private final BookingRepository bookingRepository;
	private final RunTimeRepository runTimeRepository;
	private final BookingSeatRepository bookingSeatRepository;
	private final SeatRepository seatRepository;


	// 영화 예매 예약
	@Transactional
	public BookingResponseDto createBooking(BookingRequestDto requestDto) {
		// 상영 정보 찾기 찾기
		RunTime findRunTime = getRunTime(requestDto.getRuntimeId());

		// 좌석 가능 여부 판별
		checkingBookingSeatAvaiable(requestDto);

		// 예매 생성
		List<Seat> seatList = seatRepository.findByIdList(requestDto.getBookingSeat());
		Booking booking = new Booking(BookingStatus.PENDING, null, findRunTime, seatList);
		bookingRepository.save(booking);

		return BookingResponseDto.toDto(booking, findRunTime);
	}

	private void checkingBookingSeatAvaiable(BookingRequestDto requestDto) {
		if (bookingSeatRepository.existsBookingSeatBySeatIdAndRuntime(requestDto.getRuntimeId(),
			requestDto.getBookingSeat())) {
			throw new IllegalArgumentException("해당 좌석을 예약할 수 없습니다");
		}
	}

	private RunTime getRunTime(Long runtimeId) {
		return runTimeRepository.findById(runtimeId).orElseThrow(
			() -> new IllegalArgumentException("해당 상영 시간을 찾을 수 없습니다.")
		);
	}
}
