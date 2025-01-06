package com.example.movie15.domain.movie.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 영화 고유 식별자

    @Column(nullable = false, length = 255)
    private String title; // 영화 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 줄거리

    @Column(name = "prd_year", length = 10)
    private String productionYear; // 제작연도

    @Column(length = 255)
    private String supervision; // 감독

    @Column(length = 255)
    private String company; // 제작사

    @Column(length = 30)
    private String category; // 장르

    @Column(columnDefinition = "TEXT")
    private String audit; // 심의 정보

    @Column(length = 10)
    private String status; // 상영 상태

    @OneToOne
    @JoinColumn(name = "movie_poster_id")
    private MoviePoster moviePoster; // 영화 포스터

    @Column(nullable = false)
    private Integer duration; // 상영 시간 (분)
}
