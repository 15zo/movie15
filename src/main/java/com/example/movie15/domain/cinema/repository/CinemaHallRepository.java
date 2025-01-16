package com.example.movie15.domain.cinema.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.cinema.entity.CinemaHall;

public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {
	@Query("SELECT ch FROM CinemaHall ch WHERE ch.cinema.id = :cinemaId AND ch.hall.id = :hallId")
	Optional<CinemaHall> findByCinemaIdAndHallId(
		@Param("cinemaId") Long cinemaId,
		@Param("hallId") Long hallId
	);
}
