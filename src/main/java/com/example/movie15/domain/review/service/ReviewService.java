package com.example.movie15.domain.review.service;

import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.movie.repository.MovieRepository;
import com.example.movie15.domain.review.dto.ReviewRequestDto;
import com.example.movie15.domain.review.dto.ReviewResponseDto;
import com.example.movie15.domain.review.entity.Review;
import com.example.movie15.domain.review.repository.ReviewRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * @param movieId     영화 ID (영화 정보는 추후 MovieRepository 연동 후 처리 예정)
     * @param dto         리뷰 요청 DTO
     */
    @Transactional
    public void createReview(Long loginUserId, Long movieId, ReviewRequestDto dto) {
        // user 조회
        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // movie 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // review 객체 생성
        Review review = new Review(dto.getComment(), dto.getRating());

        // 연관관계 편의메소드
//        review.setUser(user);
//        review.setMovie(movie);
//
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
    public List<ReviewResponseDto> findReviews(Long loginUserId) {

        return reviewRepository.findAllByUserIdWithMovie(loginUserId)
                .stream()
                .map(review -> new ReviewResponseDto(
                        review.getMovie().getTitle(),
                        review.getComment(),
                        review.getRating()
                ))
                .toList();
    }

    // TODO : 에러타입 수정

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
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // 리뷰작성자의 아이디와 현재로그인유저의 아이디가 다르면 에러처리
        if (!Objects.equals(review.getUser().getId(), loginUserId)) {
            throw new NotFoundException(ExceptionType.USER_NOT_FOUND); // 잘못된 유저 접근
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
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // 리뷰작성자의 아이디와 현재로그인유저의 아이디가 다르면 에러처리
        if (!Objects.equals(review.getUser().getId(), loginUserId)) {
            throw new NotFoundException(ExceptionType.USER_NOT_FOUND); // 잘못된 유저 접근
        }

        reviewRepository.delete(review);
    }
}
