package com.example.movie15.domain.movie.controller;

import java.util.List;

import com.example.movie15.domain.movie.dto.MovieReviewsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	@GetMapping("/search")
	public ResponseEntity<Page<MovieResponseDto>> searchMovies(@RequestParam(required = false) String title,
															   @RequestParam(required = false) String genre,
															   @RequestParam(defaultValue = "0") int page,
															   @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<MovieResponseDto> moviePage = movieService.searchMovies(title, genre, pageable);
		return ResponseEntity.ok(moviePage);
	}

	@GetMapping("/playing")
	public ResponseEntity<List<MovieResponseDto>> getCurrentlyPlayingMovies() {
		List<MovieResponseDto> movies = movieService.getCurrentlyPlayingMovies();
		return ResponseEntity.ok(movies);
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

	/**
	 * 영화 ID에 해당하는 리뷰 목록을 조회.
	 *
	 * @param movieId 조회할 영화의 ID
	 * @return 영화에 대한 리뷰 목록을 포함하는 ResponseEntity. 영화에 대한 리뷰가 없으면 빈 리스트를 반환.
	 */
	@GetMapping("/{movieId}/reviews")
	public ResponseEntity<Page<MovieReviewsResponseDto>> findMovieReviews(
			@PathVariable Long movieId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction
	) {
		Sort sort = direction.equalsIgnoreCase("desc") ?
				Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<MovieReviewsResponseDto> reviewList = movieService.findMovieReviews(movieId, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(reviewList);
	}

}
