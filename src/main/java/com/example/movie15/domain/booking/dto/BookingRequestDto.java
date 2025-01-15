package com.example.movie15.domain.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;

@Getter
public class BookingRequestDto {

	private Long runtimeId;

	private List<Long> bookingSeat;
}
