package com.example.movie15.domain.review.service;

import com.example.movie15.domain.review.entity.Review;
import com.example.movie15.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
}
