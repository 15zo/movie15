package com.example.movie15.domain.cinema.entity;

import com.example.movie15.domain.booking.entity.BookingSeat;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좌석 고유 식별자

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall; // 상영관 식별자

    @Column(nullable = false)
    private Integer rowNum; // 행 번호

    @Column(nullable = false)
    private Integer colNum; // 열 번호

    @Column(nullable = false, length = 20)
    private String type; // 좌석 타입 (예: 일반석, VIP석)

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();
}
