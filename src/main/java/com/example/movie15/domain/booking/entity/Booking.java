package com.example.movie15.domain.booking.entity;

import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public Booking(BookingStatus bookingStatus, Payment payment, List<BookingSeat> bookingSeatList) {
        this.bookingStatus = bookingStatus;
        this.payment = payment;
        this.bookingSeatList = bookingSeatList;
    }
}
