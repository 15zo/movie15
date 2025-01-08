package com.example.movie15.domain.movie.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	default Movie findByIdOrElseThrow(Long movieId){
		return findById(movieId).orElseThrow(()-> new NotFoundException(ExceptionType.MOVIE_NOT_FOUND));
	}
}
