package com.example.movie15.domain.review.entity;

import com.example.movie15.domain.movie.entity.Movie;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.global.entity.BaseEntity;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Getter
@Entity
public class Review extends BaseEntity {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    public Review() {}

    public Review(String comment, Integer rating, User user, Movie movie) {
        this.comment = comment;
        this.rating = rating;
        this.user = user;
        this.movie = movie;
    }

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */


    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    // 엔티티 수정 메소드
    public void updateReview(String comment, Integer rating) {
        if (Objects.equals(this.comment, comment) && Objects.equals(this.rating, rating)) {
            throw new NotFoundException(ExceptionType.SAME_REVIEW); // 이미 같은 리뷰임.
        }

        this.comment = comment;
        this.rating = rating;
    }
}
