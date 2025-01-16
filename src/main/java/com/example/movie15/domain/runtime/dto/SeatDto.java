package com.example.movie15.domain.runtime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDto {
	private Long seatId;
	private String seatNumber; // A1, B2 같은 형식
	private boolean status;    // 예약 가능 여부
	private String type;       // 좌석 타입 (예: 일반, VIP 등)
}