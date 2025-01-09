package com.example.movie15.domain.cinema.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.movie15.domain.movie.entity.RunTime;
import com.example.movie15.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상영관 고유 식별자

    @ManyToOne
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema; // 영화관 참조

    @Column(nullable = false, length = 50)
    private String name; // 상영관 이름 (예: "1관")

    @Column(nullable = false)
    private Integer seatCount; // 좌석 수

    @OneToMany(mappedBy = "hall") // RunTime과 1:N 관계
    private List<RunTime> runTimes = new ArrayList<>();

}
