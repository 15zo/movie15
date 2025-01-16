package com.example.movie15.domain.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class PaymentDto {

	public PaymentDto(String orderId, String paymentKey, Long amount) {
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.amount = amount;
	}

	private String paymentKey;
	private String orderId;
	private Long amount;
}
