package com.example.movie15.domain.movie.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 영화 고유 식별자 (DB 내부 ID)

    @Column(nullable = false, length = 255)
    private String title; // 영화 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 줄거리

    @Column(name = "prd_year", length = 10)
    private String productionYear; // 제작연도

    @Column(length = 30)
    private String genre; // 장르

    @Column(length = 10)
    private String status; // 상영 상태 (e.g., 'Released')

    @Column
    private String moviePosterUrl; // 영화 포스터

    @Column
    private String trailerUrl;

    @Column
    private Integer duration; // 상영 시간 (분)

    // 모든 필드를 초기화하는 생성자
    public Movie(String title, String content, String productionYear, Integer duration, String genre, String status, String moviePosterUrl) {
        this.title = title;
        this.content = content;
        this.productionYear = productionYear;
        this.duration = duration;
        this.genre = genre;
        this.status = status;
        this.moviePosterUrl = moviePosterUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

}
