package com.example.movie15.domain.payment.entity;

import com.example.movie15.domain.booking.enums.PaymentMethod;
import com.example.movie15.domain.booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private BigDecimal money;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
