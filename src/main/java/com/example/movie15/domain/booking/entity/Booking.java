package com.example.movie15.domain.booking.entity;

import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.cinema.entity.Seat;
import com.example.movie15.domain.payment.entity.Payment;
import com.example.movie15.domain.runtime.entity.RunTime;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToOne(fetch = LAZY)
    private Payment payment;

    @ManyToOne(fetch = LAZY)
    private RunTime runTime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();


    public void updateBookingStatus(BookingStatus bookingStatus, Payment payment) {

        switch (bookingStatus) {
            case COMPLETED -> {
                this.bookingStatus = BookingStatus.COMPLETED;
                this.payment = payment;
            }
            case CANCELED -> {
                this.bookingStatus = BookingStatus.CANCELED;
                this.payment = payment;
            }
            default -> throw new UnsupportedOperationException("Unsupported booking status: " + bookingStatus);
        }
    }

    public Booking() {
    }

    public Booking(BookingStatus bookingStatus, Payment payment, RunTime runTime, List<Seat> seatList) {
        this.bookingStatus = bookingStatus;
        this.payment = payment;

        seatList.stream()
            .map(seat -> new BookingSeat(seat, runTime, this))
            .forEach(this::addBookingSeat);
    }

    private void addBookingSeat(BookingSeat bookingSeat) {
        this.bookingSeatList.add(bookingSeat);
        bookingSeat.setBooking(this);
    }
}
