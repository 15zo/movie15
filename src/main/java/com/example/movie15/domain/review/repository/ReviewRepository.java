package com.example.movie15.domain.review.repository;

import com.example.movie15.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 사용자 ID에 해당하는 리뷰 목록 조회. N+1 방지 (JOIN FETCH)
    @Query("SELECT r FROM Review r JOIN FETCH r.movie WHERE r.user.id = :userId")
    List<Review> findAllByUserIdWithMovie(Long userId);

    // 특정 영화 ID에 해당하는 리뷰 목록 조회. N+1 방지 (JOIN FETCH)
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.movie.id = :movieId")
    List<Review> findAllByMovieIdWithUser(Long movieId);
}
