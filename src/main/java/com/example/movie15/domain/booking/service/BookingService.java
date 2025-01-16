package com.example.movie15.domain.booking.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.booking.dto.BookingRequestDto;
import com.example.movie15.domain.booking.dto.BookingResponseDto;
import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.booking.repository.BookingSeatRepository;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.entity.Seat;
import com.example.movie15.domain.runtime.repository.RunTimeRepository;
import com.example.movie15.domain.runtime.repository.SeatRepository;
import com.example.movie15.domain.user.entity.User;

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
	public BookingResponseDto createBooking(User user, BookingRequestDto requestDto) {
		// 상영 정보 찾기 찾기
		RunTime findRunTime = getRunTime(requestDto.getRuntimeId());

		// 좌석 가능 여부 판별
		checkingBookingSeatAvaiable(requestDto);

		// 예매 생성
		List<Seat> seatList = seatRepository.findByIdList(requestDto.getBookingSeat());

		Booking booking = new Booking(BookingStatus.PENDING, findRunTime, user, seatList);
		bookingRepository.save(booking);

		return BookingResponseDto.toDto(booking, findRunTime);
	}

	// 영화 예매 조회
	public List<BookingResponseDto> findAllBooking(User user, Pageable pageable) {

		List<Booking> bookingList = bookingRepository.findBookingsByUserId(user.getId(), pageable).getContent();

		return bookingList.stream()
			.map(booking -> BookingResponseDto.toDto(booking, booking.getRunTime()))
			.toList();
	}

	// 영화 예매 취소
	@Transactional
	public void cancelBooking(User user, Long bookingId) {
		Booking findBooking = bookingRepository.findBookingByIdAndUserId(bookingId, user.getId());

		bookingRepository.delete(findBooking);
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
