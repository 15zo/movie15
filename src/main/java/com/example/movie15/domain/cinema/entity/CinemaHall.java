package com.example.movie15.domain.cinema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "cinema_hall", uniqueConstraints = @UniqueConstraint(columnNames = {"cinema_id", "hall_id"}))
public class CinemaHall {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cinema_id", nullable = false)
	private Cinema cinema;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hall_id", nullable = false)
	private Hall hall;

	public CinemaHall(Cinema cinema, Hall hall) {
		this.cinema = cinema;
		this.hall = hall;
	}
}
