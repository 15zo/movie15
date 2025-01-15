package com.example.movie15.domain.review.controller;

import com.example.movie15.domain.review.dto.ReviewRequestDto;
import com.example.movie15.domain.review.dto.ReviewResponseDto;
import com.example.movie15.domain.review.service.ReviewService;
import com.example.movie15.global.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 리뷰 관련 API 를 처리하는 컨트롤러 클래스.
 * 사용자가 작성, 조회, 수정, 삭제할 수 있는 리뷰 기능을 제공.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰를 작성.
     * 사용자가 특정 영화에 대한 리뷰를 작성할 때 호출되는 메소드.
     *
     * @param movieId   영화 ID
     * @param dto       리뷰 요청 DTO
     * @param userDetails 로그인한 사용자의 정보
     * @return ResponseEntity 상태 코드와 응답 메시지
     */
    @PostMapping("/movies/{movieId}")
    public ResponseEntity<String> createReview(
            @PathVariable Long movieId,
            @RequestBody @Valid ReviewRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        reviewService.createReview(loginUserId, movieId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰작성완료");
    }

    /**
     * 로그인한 유저의 "본인 리뷰"를 조회.
     * 사용자가 자신이 작성한 리뷰 목록을 조회할 때 호출되는 메소드.
     *
     * @param userDetails 로그인한 사용자의 정보
     * @return ResponseEntity 리뷰 목록과 상태 코드
     */
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> findReviews(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Long loginUserId = userDetails.getUser().getId();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.findReviews(loginUserId, pageable));
    }

    /**
     * 리뷰를 수정.
     * 사용자가 본인이 작성한 리뷰를 수정할 때 호출되는 메소드.
     *
     * @param reviewId  수정할 리뷰의 ID
     * @param dto       리뷰 요청 DTO (수정 내용)
     * @param userDetails 로그인한 사용자의 정보
     * @return ResponseEntity 수정 완료 메시지와 상태 코드
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        reviewService.updateReview(reviewId, loginUserId, dto);

        return ResponseEntity.status(HttpStatus.OK).body("수정완료");
    }

    /**
     * 리뷰를 삭제.
     * 사용자가 본인이 작성한 리뷰를 삭제할 때 호출되는 메소드.
     *
     * @param reviewId  삭제할 리뷰의 ID
     * @param userDetails 로그인한 사용자의 정보
     * @return ResponseEntity 삭제 완료 메시지와 상태 코드
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        reviewService.deleteReview(loginUserId, reviewId);

        return ResponseEntity.status(HttpStatus.OK).body("삭제완료");
    }
}
