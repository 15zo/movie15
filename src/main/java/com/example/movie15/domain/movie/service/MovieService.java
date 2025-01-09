package com.example.movie15.domain.movie.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.movie15.domain.movie.dto.GenreDto;
import com.example.movie15.domain.movie.dto.MovieDetailsResponseDto;
import com.example.movie15.domain.movie.dto.MovieDto;
import com.example.movie15.domain.movie.dto.MovieResponseDto;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

	private final MovieRepository movieRepository;
	private final TmdbService tmdbService;

	@Transactional
	public Movie saveMovieFromTmdb(Long tmdbId) {
		// TMDB API에서 영화 데이터 가져오기
		MovieDto tmdbMovie = tmdbService.getMovieDetails(tmdbId);

		// 예고편 URL 가져오기
		String trailerUrl = tmdbService.getTrailerUrl(tmdbId);

		String genre = extractFirstGenre(tmdbMovie.getGenres());

		// Movie 엔티티 생성
		Movie movie = new Movie(
			tmdbMovie.getTitle(),
			tmdbMovie.getOverview(),
			tmdbMovie.getRelease_date() != null ? tmdbMovie.getRelease_date() : null,
			tmdbMovie.getRuntime() != null ? tmdbMovie.getRuntime() : 0, // runtime 처리
			genre,
			tmdbMovie.getStatus(),
			tmdbMovie.getPoster_path() != null ? "https://image.tmdb.org/t/p/w500" + tmdbMovie.getPoster_path() : null
		);
		movie.setTrailerUrl(trailerUrl);

		return movieRepository.save(movie);
	}

	@Transactional
	public void savePopularMovies() {
		int totalPages = 2; // 가져올 페이지 수 (한 페이지에 20개 영화)
		for (int page = 1; page <= totalPages; page++) {
			List<MovieDto> popularMovies = tmdbService.getPopularMovies(page);

			for (MovieDto movieSummary : popularMovies) {
				// 상세 정보 가져오기
				MovieDto movieDetails = tmdbService.getMovieDetails(movieSummary.getId());

				// 예고편 URL 가져오기
				String trailerUrl = tmdbService.getTrailerUrl(movieSummary.getId());
				// 첫 번째 장르 추출
				String genre = extractFirstGenre(movieDetails.getGenres());

				// Movie 엔티티 생성
				Movie movie = new Movie(
					movieDetails.getTitle(),
					movieDetails.getOverview(),
					movieDetails.getRelease_date() != null ? movieDetails.getRelease_date() : null,
					movieDetails.getRuntime() != null ? movieDetails.getRuntime() : 0, // runtime 처리
					genre,
					movieDetails.getStatus(),
					movieDetails.getPoster_path() != null ?
						"https://image.tmdb.org/t/p/w500" + movieDetails.getPoster_path() : null
				);
				movie.setTrailerUrl(trailerUrl);

				// DB에 저장
				movieRepository.save(movie);
			}
		}
	}

	public Page<MovieResponseDto> findAllMovies(Pageable pageable) {

		return movieRepository.findAllMoviesWithPagination(pageable);
	}

	public Page<MovieResponseDto> searchMovies(String title, String genre, Pageable pageable) {
		Page<Movie> movies = movieRepository.searchMovies(title,genre,pageable);
		return movies.map(this::convertToMovieResponseDto);
	}

	private MovieResponseDto convertToMovieResponseDto(Movie movie) {
		return new MovieResponseDto(
			movie.getId(),
			movie.getTitle(),
			movie.getProductionYear(),
			movie.getGenre(),
			movie.getMoviePosterUrl(),
			movie.getDuration()
		);
	}

	private String extractFirstGenre(List<GenreDto> genres) {
		if (genres != null && !genres.isEmpty()) {
			return genres.get(0).getName(); // 첫 번째 장르의 이름 반환
		}
		return "장르 정보 없음"; // 기본값
	}

	public MovieDetailsResponseDto findMovie(Long movieId) {
		Movie movie = movieRepository.findByIdOrElseThrow(movieId);
		return convertToDto(movie);
	}

	private MovieDetailsResponseDto convertToDto(Movie movie) {
		MovieDetailsResponseDto dto = new MovieDetailsResponseDto();
		dto.setId(movie.getId());
		dto.setTitle(movie.getTitle());
		dto.setOverview(movie.getContent());
		dto.setRelease_date(movie.getProductionYear());
		dto.setRuntime(movie.getDuration());
		dto.setStatus(movie.getStatus());
		dto.setGenres(movie.getGenre());
		dto.setPoster_path(movie.getMoviePosterUrl());
		dto.setTrailerUrl(movie.getTrailerUrl());
		return dto;
	}

	public void deleteMovie(Long movieId) {
		movieRepository.findByIdOrElseThrow(movieId);
		movieRepository.deleteById(movieId);
	}


}