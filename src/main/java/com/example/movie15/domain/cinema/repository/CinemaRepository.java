package com.example.movie15.domain.cinema.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.cinema.dto.CinemaResponseDto;
import com.example.movie15.domain.cinema.entity.Cinema;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {


		default Cinema findByIdOrElseThrow(Long cinemaId){
		return  findById(cinemaId).orElseThrow(()-> new NotFoundException(ExceptionType.CINEMA_NOT_FOUND));

	}
	@Modifying
	@Query("DELETE FROM CinemaHall ch WHERE ch.cinema.id = :cinemaId AND ch.hall.id = :hallId")
	void deleteRelation(@Param("cinemaId") Long cinemaId,@Param("hallId") Long hallId);

	@Query("SELECT COUNT(ch) > 0 FROM CinemaHall ch WHERE ch.cinema.id = :cinemaId AND ch.hall.id = :hallId")
	boolean existsRelation(@Param("cinemaId") Long cinemaId, @Param("hallId") Long hallId);

	@Query("SELECT c FROM Cinema c JOIN FETCH c.cinemaHalls ch JOIN FETCH ch.hall WHERE c.location = :location")
	List<Cinema> findByLocation(@Param("location") String location);
}
