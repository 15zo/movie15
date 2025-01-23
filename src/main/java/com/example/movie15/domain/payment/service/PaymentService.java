package com.example.movie15.domain.payment.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.booking.enums.PaymentMethod;
import com.example.movie15.domain.booking.enums.PaymentStatus;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.payment.entity.Payment;
import com.example.movie15.domain.payment.repository.PaymentRepository;
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
	private String secretKey;

	@Value("${payment.toss.url}")
	private String tossUrl;

	// 결제 성공
	@Transactional
	public void paymentSuccessLogic(String paymentKey, Long bookingId, Long amount) {

		// 예약 정보를 찾아오기
		Booking booking = getBooking(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(BigDecimal.valueOf(amount), paymentKey, PaymentMethod.TOSS,
			PaymentStatus.COMPLETE);

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.COMPLETED, payment);
	}

	// 결제 실패
	@Transactional
	public void tossPaymentFail(Long bookingId) {
		// 예약 정보를 찾아오기
		Booking booking = getBooking(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(null, null, PaymentMethod.TOSS, PaymentStatus.FAIL);

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.PENDING, payment);
	}

	// 결제 취소
	@Transactional
	public void paymentCancelLogic(Long bookingId, String paymentKey, String cancelReason) {
		// 예약 정보를 찾아오기
		Booking booking = bookingRepository.findBookingWithPayment(bookingId);

		// 결제 정보 저장
		Payment payment = new Payment(booking.getPayment().getMoney(), booking.getPayment().getPaymentKey(),
			booking.getPayment().getPaymentMethod(), booking.getPayment().getPaymentStatus());

		// 예약 정보 결제 정보 저장
		booking.updateBookingStatus(BookingStatus.CANCELED, payment);
	}

	private Booking getBooking(Long bookingId) {
		return bookingRepository.findById(bookingId)
			.orElseThrow(() -> new NotFoundException(ExceptionType.BOOKING_NOT_FOUND));
	}


}
