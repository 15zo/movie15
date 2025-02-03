package com.example.movie15.domain.booking.repository;

import java.util.Optional;

import com.example.movie15.domain.booking.entity.Booking;

import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b JOIN FETCH b.payment WHERE b.id = :bookingId")
	Booking findBookingWithPayment(@Param("bookingId") Long bookingId);

	@Query("SELECT b FROM Booking b "
		+ "JOIN FETCH b.bookingSeatList bs "
		+ "JOIN FETCH bs.seat s "
		+ "WHERE b.user.id = :userId")
	Page<Booking> findBookingsByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT b FROM Booking b "
		+ "JOIN FETCH b.bookingSeatList bs "
		+ "JOIN FETCH bs.seat s "
		+ "WHERE b.id = :bookingId AND b.user.id = :userId")
	Booking findBookingByIdAndUserId(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

	// RabbitMQ 에서 사용
	@Query("SELECT b FROM Booking b JOIN FETCH b.user WHERE b.id = :bookingId")
	Optional<Booking> findByIdWithUser(@Param("bookingId") Long bookingId);

	// RabbitMQ 에서 사용
	default Booking findBookingWithUser(Long bookingId) {
		return findByIdWithUser(bookingId).orElseThrow(() -> new NotFoundException(ExceptionType.BOOKING_NOT_FOUND));
	}
}
