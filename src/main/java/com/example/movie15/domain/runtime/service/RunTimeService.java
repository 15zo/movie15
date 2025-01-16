package com.example.movie15.domain.runtime.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.runtime.entity.Seat;
import com.example.movie15.domain.cinema.repository.CinemaHallRepository;
import com.example.movie15.domain.cinema.repository.CinemaRepository;
import com.example.movie15.domain.cinema.repository.HallRepository;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;
import com.example.movie15.domain.runtime.dto.MovieScheduleDto;
import com.example.movie15.domain.runtime.dto.RunTimeRequestDto;
import com.example.movie15.domain.runtime.dto.RunTimeResponseDto;
import com.example.movie15.domain.runtime.dto.SeatDto;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.repository.RunTimeRepository;
import com.example.movie15.domain.runtime.repository.SeatRepository;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunTimeService {
	private final RunTimeRepository runTimeRepository;
	private final MovieRepository movieRepository;
	private final HallRepository hallRepository;
	private final CinemaHallRepository cinemaHallRepository;
	private final CinemaRepository cinemaRepository;
	private final SeatRepository seatRepository;
	@Transactional
	public RunTimeResponseDto createRunTime(RunTimeRequestDto requestDto) {
		var cinemaHall = cinemaHallRepository.findByCinemaIdAndHallId(requestDto.getCinemaId(), requestDto.getHallId())
			.orElseThrow(() -> new NotFoundException(ExceptionType.CINEMA_HALL_NOT_FOUND));

		hallRepository.findByIdOrElseThrow(requestDto.getHallId());
		Movie movie = movieRepository.findByIdOrElseThrow(requestDto.getMovieId());

		// 유지보수 시간 추가
		int maintenanceMinutes = 15; // 유지보수 시간
		int runningTimeMinutes = movie.getRuntimeMinutes(); // 영화 런타임
		LocalTime endTime = requestDto.getStartTime().plusMinutes(runningTimeMinutes + maintenanceMinutes);

		// 중복 런타임 개수 확인
		long overlappingCount = runTimeRepository.countOverlappingRunTimes(
			cinemaHall.getId(),
			requestDto.getDate(),
			requestDto.getStartTime(),
			endTime
		);

		// 중복된 경우 예외 발생
		if (overlappingCount > 0) {
			throw new BadValueException(ExceptionType.RUN_TIME_BAD_REQUEST);
		}

		// 런타임 저장
		RunTime runTime = new RunTime(cinemaHall, movie, requestDto.getDate(), requestDto.getStartTime(), endTime, requestDto.getAmount());
		RunTime savedRunTime = runTimeRepository.save(runTime);

		return RunTimeResponseDto.toDto(savedRunTime);
	}

	public List<MovieScheduleDto> getMovieSchedule(Long cinemaId, Long movieId, LocalDate date) {
		cinemaRepository.findByIdOrElseThrow(cinemaId);
		movieRepository.findByIdOrElseThrow(movieId);
		List<RunTime> runTimes = runTimeRepository.findByCinemaIdAndMovieIdAndDateOrElseThrow(cinemaId, movieId, date);

		return runTimes.stream()
			.map(runTime -> new MovieScheduleDto(
				runTime.getId(),
				runTime.getCinemaHall().getHall().getName(),
				runTime.getStartTime(),
				runTime.getEndTime()))
			.collect(Collectors.toList());
	}

	public List<SeatDto> getSeatsByRunTime(Long runtimeId) {
		List<Seat> seats = seatRepository.findSeatsByRunTimeId(runtimeId);

		return seats.stream()
			.map(seat -> new SeatDto(
				seat.getId(),
				seat.getFormattedSeatNumber(),
				seat.getStatus(),
				seat.getType().name()
			))
			.collect(Collectors.toList());
	}

	public void deleteRunTime(Long runtimeId) {
		RunTime runTime = runTimeRepository.findByIdOrElseThrow(runtimeId);
		runTimeRepository.delete(runTime);
	}
}
