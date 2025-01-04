package com.example.movie15.domain.cinema.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int normalSeatCol;

    private int normalSeatRow;

    private int specialSeatCol;

    private int specialSeatRow;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @OneToMany(mappedBy = "hall")
    private List<Seat> seatList = new ArrayList<>();

}
