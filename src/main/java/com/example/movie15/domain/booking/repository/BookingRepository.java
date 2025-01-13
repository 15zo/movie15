package com.example.movie15.domain.booking.repository;

import java.util.Optional;

import com.example.movie15.domain.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b JOIN FETCH b.payment WHERE b.id = :bookingId")
	Booking findBookingWithPayment(@Param("bookingId") Long bookingId);

}
