package com.example.movie15.domain.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.movie15.domain.booking.entity.BookingSeat;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

	@Query("SELECT COUNT(bs) > 0 FROM BookingSeat bs WHERE bs.seat.id IN :seatIds AND bs.runtime.id = :runtimeId")
	boolean existsBookingSeatBySeatIdAndRuntime(
		@Param("runtimeId") Long runtimeId,
		@Param("seatIds") List<Long> seatIds);
}
