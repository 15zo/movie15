package com.example.movie15.domain.review.entity;

import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.global.entity.BaseEntity;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.util.Objects;

@Getter
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;


    public Review() {}

    public Review(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }


    // 연관관계 편의메소드

//    public void setUser(User user) {
//        this.user = user;
//        user.getReviews.add(this);
//    }
//
//    public void setMovie(Movie movie) {
//        this.movie = movie;
//        movie.getReviews.add(this);
//    }

    // TODO : 에러타입 수정해야함
    // 엔티티 수정 메소드
    public void updateReview(String comment, Integer rating) {
        if (Objects.equals(this.comment, comment) && Objects.equals(this.rating, rating)) {
            throw new NotFoundException(ExceptionType.USER_NOT_FOUND); // 이미 같은 리뷰임.
        }

        this.comment = comment;
        this.rating = rating;
    }

}
