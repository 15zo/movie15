package com.example.movie15.domain.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class PaymentDto {

	public PaymentDto(Long orderId, String paymentKey, Long amount) {
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.amount = amount;
	}

	private final String paymentKey;
	private final Long orderId;
	private final Long amount;

	// 토스 결제 시 orderId는 6자 이상 64자 이하의 문자열
	public String getFormatToSixDigits() {

		if (String.valueOf(orderId).length() > 5) {
			return orderId.toString();
		}else
			return String.format("%06d", orderId);
	}
}
