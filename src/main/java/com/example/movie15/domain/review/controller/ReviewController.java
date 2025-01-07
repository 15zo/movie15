package com.example.movie15.domain.review.controller;

import com.example.movie15.domain.review.dto.ReviewRequestDto;
import com.example.movie15.domain.review.dto.ReviewResponseDto;
import com.example.movie15.domain.review.service.ReviewService;
import com.example.movie15.global.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰작성
    @PostMapping("/movies/{movieId}")
    public ResponseEntity<String> createReview(
            @PathVariable Long movieId,
            @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        reviewService.createReview(loginUserId, movieId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰작성완료");
    }

    // 리뷰조회 (로그인한 유저의 "본인리뷰" 조회)
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> findReviews(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.findReviews(loginUserId));
    }

    // 리뷰수정 (본인리뷰만 수정가능)
    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();
        reviewService.updateReview(reviewId, loginUserId, dto);

        return ResponseEntity.status(HttpStatus.OK).body("수정완료");
    }

    // 리뷰삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long loginUserId = userDetails.getUser().getId();

        reviewService.deleteReview(loginUserId, reviewId);

        return ResponseEntity.status(HttpStatus.OK).body("삭제완료");
    }
}
