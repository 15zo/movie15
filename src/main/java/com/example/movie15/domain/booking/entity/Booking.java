package com.example.movie15.domain.booking.entity;

import com.example.movie15.domain.booking.enums.BookingStatus;
import com.example.movie15.domain.runtime.entity.Seat;
import com.example.movie15.domain.payment.entity.Payment;
import com.example.movie15.domain.runtime.entity.RunTime;
import com.example.movie15.domain.runtime.model.SeatType;
import com.example.movie15.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@SQLDelete(sql = "UPDATE booking SET is_deleted = true WHERE id = ?")
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @ManyToOne(fetch = LAZY)
    private RunTime runTime;

    @ManyToOne(fetch = LAZY)
    private User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

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

    public Booking(BookingStatus bookingStatus, Payment payment, RunTime runTime, User user, List<Seat> seatList) {
        this.bookingStatus = bookingStatus;
        this.payment = payment;
        this.runTime = runTime;
        this.user = user;

        seatList.stream()
            .map(seat -> new BookingSeat(seat, runTime, this))
            .forEach(this::addBookingSeat);
    }

    public Booking(BookingStatus bookingStatus, RunTime runTime, User user, List<Seat> seatList) {
        this.bookingStatus = bookingStatus;
        this.runTime = runTime;
        this.user = user;


        seatList.stream()
            .map(seat -> new BookingSeat(seat, runTime, this))
            .forEach(this::addBookingSeat);

        BigDecimal amount = seatList.stream()
            .map(seat -> {
                if (seat.getType().equals(SeatType.VIP))
                    return runTime.getAmount().multiply(BigDecimal.valueOf(SeatType.VIP.getPriceRadio()));
                else if (seat.getType().equals(SeatType.ECONOMY))
                    return runTime.getAmount().multiply(BigDecimal.valueOf(SeatType.ECONOMY.getPriceRadio()));
                else
                    return runTime.getAmount();
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.payment = new Payment(amount);
    }

    private void addBookingSeat(BookingSeat bookingSeat) {
        this.bookingSeatList.add(bookingSeat);
        bookingSeat.setBooking(this);
    }
}
