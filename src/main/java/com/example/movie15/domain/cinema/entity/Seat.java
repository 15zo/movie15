package com.example.movie15.domain.cinema.entity;

import com.example.movie15.domain.booking.entity.BookingSeat;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false) // 상영관과 다대일 관계
    private Hall hall;

    @Column(nullable = false)
    private Integer rowNum; // 행 번호

    @Column(nullable = false)
    private Integer colNum; // 열 번호

    @Column(nullable = false, length = 20)
    private String type; // 좌석 타입 (예: 일반석, VIP석)

    @Column(nullable = false)
    private Boolean status; // 예약 가능 여부

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();
}
