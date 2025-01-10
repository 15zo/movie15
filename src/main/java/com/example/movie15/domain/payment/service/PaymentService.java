package com.example.movie15.domain.payment.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.booking.enums.PaymentMethod;
import com.example.movie15.domain.booking.enums.PaymentStatus;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.payment.entity.Payment;
import com.example.movie15.domain.payment.repository.PaymentRepository;
import com.example.movie15.global.exception.CustomException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;

	// 결제 상태 변경
	@Transactional
	public void tossPaymentSuccess(String paymentKey, String bookingId, Long amount) {
		// 예약 정보를 찾아오기
		Booking booking = getBooking(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(BigDecimal.valueOf(amount), paymentKey, PaymentMethod.TOSS, PaymentStatus.COMPLETE);

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.COMPLETED, payment);
	}

	private Booking getBooking(String bookingId) {
		return bookingRepository.findById(Long.valueOf(bookingId))
			.orElseThrow(() -> new NotFoundException(ExceptionType.BOOKING_NOT_FOUND));
	}
}
