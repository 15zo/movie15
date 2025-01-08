package com.example.movie15.domain.movie.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.movie15.domain.movie.dto.MovieResponseDto;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	default Movie findByIdOrElseThrow(Long movieId){
		return findById(movieId).orElseThrow(()-> new NotFoundException(ExceptionType.MOVIE_NOT_FOUND));
	}

	@Query("SELECT new com.example.movie15.domain.movie.dto.MovieResponseDto(m.id, m.title, m.productionYear, m.category, m.moviePosterUrl, m.duration) " +
		"FROM Movie m")
	Page<MovieResponseDto> findAllMoviesWithPagination(Pageable pageable);

	@Query("SELECT m FROM Movie m " +
		   "WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', : title, '%'))) " +
		   "AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))")
	Page<Movie> searchMovies(String title, String genre, Pageable pageable);
}
