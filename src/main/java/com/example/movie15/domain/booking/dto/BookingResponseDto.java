package com.example.movie15.domain.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.entity.BookingSeat;
import com.example.movie15.domain.cinema.dto.CinemaResponseDto;
import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.domain.runtime.entity.RunTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingResponseDto {

	private Long bookingId;
	private String movieName;
	private Long hallId;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private List<Long> bookingSeat;

	public static BookingResponseDto toDto(Booking booking, RunTime runTime) {
		List<Long> seatList = booking.getBookingSeatList().stream()
			.map(BookingSeat::getId)
			.toList();

		return new BookingResponseDto(
			booking.getId(),
			booking.getRunTime().getMovie().getTitle(),
			booking.getRunTime().getCinemaHall().getHall().getId(),
			booking.getRunTime().getDate(),
			booking.getRunTime().getStartTime(),
			booking.getRunTime().getEndTime(),
			seatList
		);
	}
}
