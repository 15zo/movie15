package com.example.movie15.domain.cinema.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {


		default Cinema findByIdOrElseTheow(Long cinemaId){
		return  findById(cinemaId).orElseThrow(()-> new NotFoundException(ExceptionType.CINEMA_NOT_FOUND));

	}
}
