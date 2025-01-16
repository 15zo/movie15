package com.example.movie15.domain.runtime.entity;

import static jakarta.persistence.FetchType.*;

import com.example.movie15.domain.booking.entity.BookingSeat;
import com.example.movie15.domain.cinema.entity.Hall;
import com.example.movie15.domain.runtime.model.SeatType;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "hall_id", nullable = false) // 상영관과 다대일 관계
    private Hall hall;

    @Column(nullable = false)
    private Integer rowNum; // 행 번호

    @Column(nullable = false)
    private Integer colNum; // 열 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatType type; // 좌석 타입 (예: 일반석, VIP석)

    @Column(nullable = false)
    private Boolean status; // 예약 가능 여부

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();


    public SeatType getSeatType() {
        return  this.type;
    };

    public String getFormattedSeatNumber() {
        // rowNum을 알파벳으로 변환 (A = 1, B = 2, ...)
        char rowChar = (char) ('A' + this.rowNum - 1); // 1부터 시작한다고 가정
        return rowChar + String.valueOf(this.colNum); // 예: 'A1', 'B2'
    }
}
