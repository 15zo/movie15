package com.example.movie15.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewRequestDto {

    @Size(max = 50, message = "리뷰는 50자 이하로 작성해야 합니다.")
    private final String comment;

    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 10, message = "평점은 10 이하이어야 합니다.")
    private final Integer rating;

    public ReviewRequestDto(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }
}
