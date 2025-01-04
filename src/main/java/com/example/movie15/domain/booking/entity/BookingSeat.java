package com.example.movie15.domain.booking.entity;

import com.example.movie15.domain.cinema.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class BookingSeat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Booking booking;
}
