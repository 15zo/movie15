package com.example.movie15.domain.movie.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie15.domain.movie.dto.MovieDetailsResponseDto;
import com.example.movie15.domain.movie.dto.MovieResponseDto;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.service.MovieService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {
	private final MovieService movieService;

	// TMDB에서 영화 단건 데이터 저장
	@GetMapping("/tmdb/{tmdbId}")
	public ResponseEntity<Movie> saveMovieFromTmdb(@PathVariable Long tmdbId) {
		Movie savedMovie = movieService.saveMovieFromTmdb(tmdbId);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
	}

	@GetMapping("/popular")
	@ResponseStatus(HttpStatus.CREATED)
	public void savePopularMoviesToDatabase() {
		movieService.savePopularMovies();
	}

	@GetMapping
	public ResponseEntity<Page<MovieResponseDto>> findAllMovies(@RequestParam(defaultValue = "0") int page,
																@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<MovieResponseDto> movies = movieService.findAllMovies(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(movies);
	}

	@GetMapping
	public ResponseEntity<Page<MovieResponseDto>> findAllMoviesFilter(@RequestParam(defaultValue = "0") int page,
																	  @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<MovieResponseDto> movies = movieService.findAllMovies(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(movies);
	}

	@GetMapping("/search")
	public ResponseEntity<Page<MovieResponseDto>> searchMovies(@RequestParam(required = false) String title,
															   @RequestParam(required = false) String genre,
															   @RequestParam(defaultValue = "0") int page,
															   @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<MovieResponseDto> moviePage = movieService.searchMovies(title, genre, pageable);
		return ResponseEntity.ok(moviePage);
	}

	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDetailsResponseDto> findMovie(@PathVariable Long movieId) {
		MovieDetailsResponseDto movies = movieService.findMovie(movieId);
		return ResponseEntity.status(HttpStatus.OK).body(movies);
	}

	@DeleteMapping("/{movieId}")
	public ResponseEntity<String> deleteMovie(@PathVariable Long movieId) {
		movieService.deleteMovie(movieId);
		return ResponseEntity.status(HttpStatus.OK).body("삭제완료");
	}

}
