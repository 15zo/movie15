package com.example.movie15.domain.runtime.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.domain.movie.entity.Movie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class RunTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 상영 시간 고유 식별자

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie; // 영화 ID 참조

	@ManyToOne
	@JoinColumn(name = "hall_id", nullable = false)
	private Hall hall; // 상영관 참조

	@Column(nullable = false)
	private LocalTime startTime; // 상영 시작 시간

	@Column(nullable = false)
	private LocalTime endTime; // 상영 종료 시간

	@Column(nullable = false)
	private LocalDate date; // 상영 날짜

	public RunTime(Hall hall, Movie movie, LocalDate date, LocalTime startTime, LocalTime endTime) {
		this.hall = hall;
		this.movie = movie;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
