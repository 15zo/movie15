package com.example.movie15.domain.cinema.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.cinema.dto.CinemaRequestDto;
import com.example.movie15.domain.cinema.dto.CinemaResponseDto;
import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.domain.cinema.entity.CinemaHall;
import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.domain.cinema.repository.CinemaRepository;
import com.example.movie15.domain.cinema.repository.HallRepository;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CinemaService {
	private final CinemaRepository cinemaRepository;
	private final HallRepository hallRepository;

	public CinemaResponseDto createCinema(CinemaRequestDto requestDto) {

		Cinema cinema = new Cinema(requestDto.getLocation(), requestDto.getName());
		cinema = cinemaRepository.save(cinema); // Cinema 먼저 저장


		return CinemaResponseDto.toDto(cinema);
	}

	public CinemaResponseDto addHallsToCinema(Long cinemaId, List<Long> hallIds) {
		Cinema cinema = cinemaRepository.findByIdOrElseThrow(cinemaId);

		List<Hall> halls = hallRepository.findAllByIdOrElseThrow(hallIds);
//		중복 방지 및 관계 생성
		halls.forEach(hall -> {
			if (cinema.getCinemaHalls().stream()
				.noneMatch(cinemaHall -> cinemaHall.getHall().getId().equals(hall.getId()))) {
				CinemaHall cinemaHall = new CinemaHall(cinema, hall);
				cinema.addCinemaHall(cinemaHall);
			}
		});
	Cinema updatedCinema = cinemaRepository.save(cinema);

	return CinemaResponseDto.toDto(updatedCinema);
	}
	@Transactional
	public void deleteHallFromCinema(Long cinemaId, Long hallId) {
		if(!cinemaRepository.existsRelation(cinemaId, hallId)){
			throw new NotFoundException(ExceptionType.HALL_OR_CINEMA_NOT_FOUND);
		}
		cinemaRepository.findByIdOrElseThrow(cinemaId);
		hallRepository.findByIdOrElseThrow(hallId);
		cinemaRepository.deleteRelation(cinemaId, hallId);
	}
}
