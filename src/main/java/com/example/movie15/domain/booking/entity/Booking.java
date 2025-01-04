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
    private Long Id;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToOne(fetch = LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeatList = new ArrayList<>();


}
