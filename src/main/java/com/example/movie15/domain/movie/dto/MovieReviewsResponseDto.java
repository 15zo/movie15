package com.example.movie15.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieReviewsResponseDto {

    private final String userNickname;
    private final String comment;
    private final Integer rating;
}
