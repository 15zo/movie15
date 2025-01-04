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
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();
}
