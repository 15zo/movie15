package com.example.movie15.domain.review.service;

import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;
import com.example.movie15.domain.review.dto.ReviewRequestDto;
import com.example.movie15.domain.review.dto.ReviewResponseDto;
import com.example.movie15.domain.review.entity.Review;
import com.example.movie15.domain.review.repository.ReviewRepository;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import com.example.movie15.global.exception.WrongAccessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    /**
     * 리뷰를 생성.
     * 사용자가 특정 영화에 대한 리뷰를 작성할 때 호출.
     *
     * @param loginUserId 로그인한 사용자의 ID
     * @param movieId     영화 ID
     * @param dto         리뷰 요청 DTO
     */
    @Transactional
    public void createReview(Long loginUserId, Long movieId, ReviewRequestDto dto) {
        // user 조회
        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // movie 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.MOVIE_NOT_FOUND));

        // 사용자는 하나의 영화에 하나의 리뷰만 생성 가능
        if (reviewRepository.existsByUserIdAndMovieId(loginUserId, movieId)) {
            throw new WrongAccessException(ExceptionType.ALREADY_REVIEW);
        }

        // review 객체 생성
        Review review = new Review(dto.getComment(), dto.getRating());

        // 연관관계 편의메소드
        review.setUser(user);
        review.setMovie(movie);

        // review 저장
        reviewRepository.save(review);
    }

    /**
     * 로그인한 사용자의 리뷰 목록을 조회.
     * 해당 사용자가 작성한 리뷰를 조회할 때 호출.
     *
     * @param loginUserId 로그인한 사용자의 ID
     * @return 사용자가 작성한 리뷰 목록
     */
    public Page<ReviewResponseDto> findReviews(Long loginUserId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByUserIdWithMovie(loginUserId, pageable);
        if (reviews.isEmpty()) {
            return Page.empty();
        }

        return reviews
                .map(review -> new ReviewResponseDto(
                        review.getMovie().getTitle(),
                        review.getComment(),
                        review.getRating()
                ));
    }

    /**
     * 리뷰를 수정.
     * 사용자가 본인이 작성한 리뷰를 수정할 때 호출.
     *
     * @param reviewId    수정할 리뷰의 ID
     * @param loginUserId 로그인한 사용자의 ID
     * @param dto         수정된 리뷰 정보
     */
    @Transactional
    public void updateReview(Long reviewId, Long loginUserId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.REVIEW_NOT_FOUND));

        // 리뷰작성자의 아이디와 현재로그인유저의 아이디가 다르면 에러처리
        if (!Objects.equals(review.getUser().getId(), loginUserId)) {
            throw new WrongAccessException(ExceptionType.FORBIDDEN_ACTION); // 잘못된 유저 접근
        }

        // 업데이트
        review.updateReview(dto.getComment(), dto.getRating());
    }

    /**
     * 리뷰를 삭제.
     * 사용자가 본인이 작성한 리뷰를 삭제할 때 호출.
     *
     * @param loginUserId 로그인한 사용자의 ID
     * @param reviewId    삭제할 리뷰의 ID
     */
    @Transactional
    public void deleteReview(Long loginUserId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.REVIEW_NOT_FOUND));

        User loginUser = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // 리뷰 작성자 ID 비교 및 관리자 권한 체크
        boolean isReviewOwner = Objects.equals(review.getUser().getId(), loginUserId);
        boolean isAdmin = Objects.equals(loginUser.getRole(), Role.ADMIN);

        if (isReviewOwner || isAdmin) {
            reviewRepository.delete(review);
        } else {
            throw new WrongAccessException(ExceptionType.FORBIDDEN_ACTION);
        }
    }

}