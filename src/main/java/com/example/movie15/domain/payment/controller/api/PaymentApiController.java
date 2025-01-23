package com.example.movie15.domain.payment.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import com.example.movie15.domain.payment.dto.PaymentDto;
import com.example.movie15.domain.payment.service.PaymentService;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentApiController {

	private final PaymentService paymentService;

	private final RestClient restClient;

	// 결제 완료 로직
	@PostMapping( "/toss/confirm")
	public ResponseEntity<String> successPayment(
		@RequestBody PaymentDto requestDto) {

		// 토스 결제 승인 요청 실행
		tossPaymentConfirm(requestDto.getOrderId(), requestDto.getPaymentKey(), requestDto.getAmount());

		// 결제 성공 로직 수행
		paymentService.paymentSuccessLogic(requestDto.getPaymentKey(), requestDto.getOrderId(), requestDto.getAmount());

		return ResponseEntity.status(HttpStatus.OK).body("예매가 완료 되었습니다");
	}

	@PostMapping("/booking/{bookingId}/payment")
	public ResponseEntity<Void> tossPaymentCancel(
		@PathVariable Long bookingId,
		@RequestParam String paymentKey,
		@RequestParam String cancelReason) {

		// 토스 결제 취소 요청 실행
		tossPaymentCancel(paymentKey, cancelReason);

		// 결제 취소 로직 수행
		paymentService.paymentCancelLogic(bookingId, paymentKey, cancelReason);

		return ResponseEntity.ok().body(null);
	}

	// 토스 결제 승인 요청 실행
	public void tossPaymentConfirm(Long orderId, String paymentKey, Long amount) {
		PaymentDto paymentDto = new PaymentDto(orderId, paymentKey, amount);

		String responseDto = restClient.post()
			.uri("confirm")
			.body(paymentDto)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
				throw new BadValueException(ExceptionType.CHECKOUT_FAIL);
			}))
			.body(String.class);
	}

	// 토스 취소 승인 요청 실행
	public void tossPaymentCancel(String paymentKey, String cancelReason) {

		String responseDto = restClient.post()
			.uri(paymentKey + "/cancel")
			.body(cancelReason)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
				throw new BadValueException(ExceptionType.CHECKOUT_FAIL);
			}))
			.body(String.class);
	}

}