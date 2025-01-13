package com.example.movie15.domain.payment.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

	@Value("${payment.toss.secretKey}")
	private final String secretKey;

	// 결제 성공
	@Transactional
	public void tossPaymentSuccess(String paymentKey, String bookingId, Long amount) {
		// 예약 정보를 찾아오기
		Booking booking = getBooking(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(BigDecimal.valueOf(amount), paymentKey, PaymentMethod.TOSS, PaymentStatus.COMPLETE);

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.COMPLETED, payment);
	}

	// 결제 실패
	@Transactional
	public void tossPaymentFail(String bookingId) {
		// 예약 정보를 찾아오기
		Booking booking = getBooking(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(null, null, PaymentMethod.TOSS, PaymentStatus.FAIL);

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.PENDING, payment);
	}



	private Booking getBooking(String bookingId) {
		return bookingRepository.findById(Long.valueOf(bookingId))
			.orElseThrow(() -> new NotFoundException(ExceptionType.BOOKING_NOT_FOUND));
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		String encodedAuthKey = new String(
			Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
		headers.setBasicAuth(encodedAuthKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}
}
