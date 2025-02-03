package com.example.movie15.domain.movie.entity;

import com.example.movie15.domain.review.entity.Review;

import java.time.LocalDate;
import java.util.List;

import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "movie")
@SQLDelete(sql = "UPDATE movie SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
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

    @Column
    private String moviePosterUrl; // 영화 포스터

    @Column
    private String trailerUrl;

    @Column
    private Integer duration; // 상영 시간 (분)

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "removed_at")
    private LocalDate removedAt; // 영화가 Top 40에서 제거된 시점

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunTime> runTimes;

    // 모든 필드를 초기화하는 생성자
    public Movie(String title, String content, String productionYear, Integer duration, String genre, String moviePosterUrl) {
        this.title = title;
        this.content = content;
        this.productionYear = productionYear;
        this.duration = duration;
        this.genre = genre;
        this.moviePosterUrl = moviePosterUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public int getRuntimeMinutes() {
        return duration;
    }

    public void markAsRemoved() {
        this.removedAt = LocalDate.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.removedAt = null;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

