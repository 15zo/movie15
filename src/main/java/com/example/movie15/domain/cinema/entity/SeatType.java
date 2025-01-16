package com.example.movie15.domain.cinema.entity;

public enum SeatType {
	VIP("VIP석", 1.2),
	ECONOMY("일반석", 1);

	private final String description;
	private final double priceRadio;

	SeatType(String description, double priceRadio) {
		this.description = description;
		this.priceRadio = priceRadio;
	}

	public double getPriceRadio() {
		return priceRadio;
	}
}