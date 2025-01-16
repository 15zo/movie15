package com.example.movie15.domain.runtime.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface RunTimeRepository extends JpaRepository<RunTime, Long> {

	@Query("SELECT COUNT(r) FROM RunTime r WHERE r.cinemaHall.id = :cinemaHallId AND r.date = :date " +
		"AND (r.startTime < :endTime AND r.endTime > :startTime)")
	long countOverlappingRunTimes(
		@Param("cinemaHallId") Long cinemaHallId,
		@Param("date") LocalDate date,
		@Param("startTime") LocalTime startTime,
		@Param("endTime") LocalTime endTime);


	@Query("SELECT r FROM RunTime r " +
		"JOIN r.cinemaHall ch " +
		"JOIN ch.cinema c " +
		"WHERE c.id = :cinemaId " +
		"AND r.movie.id = :movieId " +
		"AND r.date = :date")
	List<RunTime> findByCinemaIdAndMovieIdAndDate(
		@Param("cinemaId") Long cinemaId,
		@Param("movieId") Long movieId,
		@Param("date") LocalDate date);

	default List<RunTime> findByCinemaIdAndMovieIdAndDateOrElseThrow(
		Long cinemaId, Long movieId, LocalDate date) {
		List<RunTime> runTimes = findByCinemaIdAndMovieIdAndDate(cinemaId, movieId, date);
		if (runTimes.isEmpty()) {
			throw new NotFoundException(ExceptionType.RUN_TIME_NOT_FOUND);
		}
		return runTimes;
	}

	default RunTime findByIdOrElseThrow(Long runtimeId){
		return findById(runtimeId).orElseThrow(()->new NotFoundException(ExceptionType.RUN_TIME_NOT_FOUND));

	}

	@Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
		"FROM RunTime rt WHERE rt.movie.id = :movieId AND rt.date >= CURRENT_DATE")
	boolean existsByMovieId(@Param("movieId") Long movieId);
}
