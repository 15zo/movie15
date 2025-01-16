package com.example.movie15.domain.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.booking.entity.Booking;
import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.booking.repository.BookingRepository;
import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.domain.cinema.entity.Seat;
import com.example.movie15.domain.cinema.repository.HallRepository;
import com.example.movie15.domain.cinema.repository.SeatRepository;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.repository.RunTimeRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.ion.Decimal;

@SpringBootTest
@Slf4j
@Transactional
class BookingServiceTest {

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HallRepository hallRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private RunTimeRepository runTimeRepository;

	@Autowired
	private UserRepository userRepository;

	@Commit
	@Test
	void 테스트진행(){
	    // given
		List<Long> seatList = List.of(1L, 2L);
		List<Seat> findSeatList = seatRepository.findByIdList(seatList);

		Hall hall = hallRepository.findById(1L).get();

		Movie movie = new Movie("title", "content", "2020", 10, "genre", "status", "movieUrl");
		movieRepository.save(movie);

		RunTime runTime = new RunTime(hall, movie, LocalDate.now(), LocalTime.now(), LocalTime.now(), Decimal.valueOf(10000));

		runTimeRepository.save(runTime);

		User user = userRepository.findByIdOrElseThrow(1L);

		Booking booking = new Booking(BookingStatus.PENDING, runTime, user, findSeatList);
		bookingRepository.save(booking);

		bookingRepository.delete(booking);
	}

	@Commit
	@Test
	void 예매취소(){
		Long bookingId = 3L;
		User user = userRepository.findByIdOrElseThrow(1L);

		Booking findBooking = bookingRepository.findBookingByIdAndUserId(bookingId, user.getId());

		bookingRepository.delete(findBooking);
	}
}