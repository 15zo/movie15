package com.example.movie15.domain.cinema.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상영관 고유 식별자

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CinemaHall> cinemaHalls = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String name; // 상영관 이름 (예: "1관")

    @Column(nullable = false)
    private Integer seatCount; // 좌석 수

    // @OneToMany(mappedBy = "hall") // RunTime과 1:N 관계
    // private List<RunTime> runTimes = new ArrayList<>();

    public Hall(String name, Integer seatCount) {
        this.name = name;
        this.seatCount = seatCount;
    }
    public Hall() {}
}
