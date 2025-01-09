package com.example.movie15.domain.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;

public interface HallRepository extends JpaRepository<Hall, Long> {

	@Query("SELECT h FROM Hall h WHERE h.id IN :ids")
	List<Hall> findAllByIds(@Param("ids") List<Long> ids);

	default List<Hall> findAllByIdOrElseThrow(List<Long> ids) {
		List<Hall> halls = findAllByIds(ids);

		//요청된 관람관 수와 반환된 관람관 수가 다르면 예외발생
		if (halls.size() != ids.size()) {
			throw new NotFoundException(ExceptionType.HALL_BAD_REQUEST);
		}
		return halls;
	}

	default void findByIdOrElseThrow(Long hallId){
		findById(hallId).orElseThrow(() ->new NotFoundException(ExceptionType.HALL_NOT_FOUND));
	}
}
