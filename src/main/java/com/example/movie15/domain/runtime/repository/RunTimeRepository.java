package com.example.movie15.domain.runtime.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.movie15.domain.runtime.entity.RunTime;

public interface RunTimeRepository extends JpaRepository<RunTime, Long> {

	@Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
		"FROM RunTime rt " +
		"JOIN CinemaHall ch ON rt.hall.id = ch.hall.id " +
		"WHERE ch.cinema.id = :cinemaId " +
		"AND ch.hall.id = :hallId " +
		"AND (:startTime < rt.endTime AND :endTime > rt.startTime)")
	boolean existsByCinemaHallAndTimeRange(Long cinemaId, Long hallId, LocalDateTime startTime, LocalDateTime endTime);
}
