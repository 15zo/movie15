package com.example.movie15.domain.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.movie15.domain.cinema.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

	@Query("select s from Seat s where s.id in :idList")
	List<Seat> findByIdList(@Param("idList") List<Long> idList);
}
