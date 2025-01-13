package com.example.movie15.domain.runtime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.runtime.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	// runTimeId를 기반으로 좌석 정보 조회
	@Query("SELECT s FROM Seat s " +
		"JOIN CinemaHall ch ON s.hall.id = ch.hall.id " +
		"JOIN RunTime rt ON rt.cinemaHall.id = ch.id " +
		"WHERE rt.id = :runTimeId")
	List<Seat> findSeatsByRunTimeId(@Param("runTimeId") Long runTimeId);
}
