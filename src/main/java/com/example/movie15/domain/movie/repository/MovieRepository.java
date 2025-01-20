package com.example.movie15.domain.movie.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.movie.dto.MovieResponseDto;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	default Movie findByIdOrElseThrow(Long movieId){
		return findById(movieId).orElseThrow(()-> new NotFoundException(ExceptionType.MOVIE_NOT_FOUND));
	}

	@Query("SELECT new com.example.movie15.domain.movie.dto.MovieResponseDto( " +
		"m.id, m.title, m.productionYear, m.genre, m.moviePosterUrl, m.duration, " +
		"CASE WHEN EXISTS (SELECT rt FROM RunTime rt WHERE rt.movie.id = m.id) THEN true ELSE false END ) " +
		"FROM Movie m")
	Page<MovieResponseDto> findAllMoviesWithPagination(Pageable pageable);

	@Query("SELECT m FROM Movie m " +
		   "WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', : title, '%'))) " +
		   "AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))")
	Page<Movie> searchMovies(String title, String genre, Pageable pageable);

	@Query("SELECT DISTINCT m FROM Movie m " +
		"JOIN RunTime r ON m.id = r.movie.id " +
		"WHERE r.date >= CURRENT_DATE " +
		"AND (r.startTime <= CURRENT_TIME AND r.endTime >= CURRENT_TIME)")
	List<Movie> findCurrentlyPlayingMovies();

	@Query("SELECT m FROM Movie m WHERE LOWER(m.title) = LOWER(:title)")
	Optional<Movie> findByTitle(String title);

	@Query(value = "SELECT * FROM movie m WHERE LOWER(m.title) = LOWER(:title)", nativeQuery = true)
	Optional<Movie> findByTitleIncludingDeleted(@Param("title") String title); // 삭제된 데이터 포함

	@Query(value = "SELECT * FROM movie m WHERE m.removed_at < :oneWeekAgo AND m.is_deleted = false", nativeQuery = true)
	List<Movie> findMoviesRemovedMoreThanAWeekAgo(@Param("oneWeekAgo") LocalDate oneWeekAgo); // 삭제된 영화 필터링
}
