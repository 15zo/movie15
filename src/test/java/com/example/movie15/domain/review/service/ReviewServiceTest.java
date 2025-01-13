package com.example.movie15.domain.review.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.example.movie15.domain.review.dto.MovieReviewsResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import com.example.movie15.domain.review.dto.ReviewRequestDto;
import com.example.movie15.domain.review.dto.ReviewResponseDto;
import com.example.movie15.domain.review.entity.Review;
import com.example.movie15.domain.review.repository.ReviewRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성 테스트 - 유효한 입력으로 리뷰 생성 성공")
    void createReview_ShouldCreateReview_WhenValidInputs() {
        // Given: 유효한 사용자와 영화 ID 및 리뷰 데이터를 준비
        Long loginUserId = 1L;
        Long movieId = 1L;

        User user = new User();
        user.setId(loginUserId);

        Movie movie = new Movie();
        movie.setId(movieId);

        ReviewRequestDto requestDto = new ReviewRequestDto("그냥그런영화", 5);

        when(userRepository.findById(loginUserId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(reviewRepository.existsByUserIdAndMovieId(loginUserId, movieId)).thenReturn(false);

        // When: 리뷰 생성 메서드 호출
        reviewService.createReview(loginUserId, movieId, requestDto);

        // Then: 리뷰가 저장되었는지 검증
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 조회 테스트 - 리뷰가 존재할 때 리뷰 반환")
    void findReviews_ShouldReturnReviews_WhenReviewsExist() {
        // Given: 특정 사용자의 리뷰 데이터가 존재
        Long loginUserId = 1L;
        Pageable pageable = Pageable.unpaged();

        Review review = new Review("그냥그런영화", 5, new User(), new Movie());
        review.getMovie().setTitle("아수라");

        Page<Review> reviews = new PageImpl<>(List.of(review));

        when(reviewRepository.findAllByUserIdWithMovie(loginUserId, pageable)).thenReturn(reviews);

        // When: 리뷰 조회 메서드 호출
        Page<ReviewResponseDto> result = reviewService.findReviews(loginUserId, pageable);

        // Then: 리뷰 데이터가 정확히 반환되었는지 확인
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("그냥그런영화");
        verify(reviewRepository, times(1)).findAllByUserIdWithMovie(loginUserId, pageable);
    }

    @Test
    @DisplayName("리뷰 수정 테스트 - 유효한 입력으로 리뷰 수정 성공")
    void updateReview_ShouldUpdateReview_WhenValidInputs() {
        // Given: 기존 리뷰와 수정할 데이터를 준비
        Long reviewId = 1L;
        Long loginUserId = 1L;

        User user = new User();
        user.setId(loginUserId);

        Review review = new Review("옛날리뷰", 3, user, new Movie());
        review.setId(reviewId);

        ReviewRequestDto requestDto = new ReviewRequestDto("리뷰업데이트", 4);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When: 리뷰 수정 메서드 호출
        reviewService.updateReview(reviewId, loginUserId, requestDto);

        // Then: 리뷰가 수정되었는지 검증
        assertThat(review.getComment()).isEqualTo("리뷰업데이트");
        assertThat(review.getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("리뷰 삭제 테스트 - 유효한 입력으로 리뷰 삭제 성공")
    void deleteReview_ShouldDeleteReview_WhenValidInputs() {
        // Given: 삭제할 리뷰 데이터를 준비
        Long reviewId = 1L;
        Long loginUserId = 1L;

        User user = new User();
        user.setId(loginUserId);

        Review review = new Review("그냥그런영화", 5, user, new Movie());
        review.setId(reviewId);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When: 리뷰 삭제 메서드 호출
        reviewService.deleteReview(loginUserId, reviewId);

        // Then: 리뷰가 삭제되었는지 검증
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("영화 리뷰 조회 테스트 - 특정 영화에 리뷰가 있을 때 리뷰 반환")
    void findMovieReviews_ShouldReturnReviews_WhenMovieHasReviews() {
        // Given: 특정 영화의 리뷰 데이터가 존재
        Long movieId = 1L;
        Pageable pageable = Pageable.unpaged();

        Review review = new Review("그냥그런영화", 5, new User(), new Movie());
        review.getUser().setName("김명호");

        Page<Review> reviews = new PageImpl<>(List.of(review));

        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(reviewRepository.findAllByMovieIdWithUser(movieId, pageable)).thenReturn(reviews);

        // When: 영화 리뷰 조회 메서드 호출
        Page<MovieReviewsResponseDto> result = reviewService.findMovieReviews(movieId, pageable);

        // Then: 영화 리뷰 데이터가 정확히 반환되었는지 확인
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserNickname()).isEqualTo("김명호");
    }
}