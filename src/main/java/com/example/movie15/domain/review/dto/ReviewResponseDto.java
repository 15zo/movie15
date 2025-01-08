package com.example.movie15.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    private final String movieTitle;
    private final String comment;
    private final Integer rating;

}
