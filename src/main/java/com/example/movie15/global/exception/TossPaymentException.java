package com.example.movie15.global.exception;

import lombok.Getter;

@Getter
public class TossPaymentException extends CustomException {

	private final ExceptionType exceptionType;
	private final String paymentKey;
	private String cancelReason;
	private Long orderId;
	private Long amount;

	public TossPaymentException(ExceptionType exceptionType,
		String paymentKey, Long orderId, Long amount) {
		super(exceptionType);
		this.exceptionType = exceptionType;
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.amount = amount;
	}


	public TossPaymentException(ExceptionType exceptionType, String cancelReason,
		String paymentKey) {
		super(exceptionType);
		this.exceptionType = exceptionType;
		this.cancelReason = cancelReason;
		this.paymentKey = paymentKey;
	}
}
