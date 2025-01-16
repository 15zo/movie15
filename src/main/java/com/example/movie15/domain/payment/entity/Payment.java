package com.example.movie15.domain.payment.entity;

import com.example.movie15.domain.booking.enums.PaymentMethod;
import com.example.movie15.domain.booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@SQLDelete(sql = "UPDATE payment SET is_deleted = true WHERE id = ?")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private BigDecimal money;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Payment() {
    }

    public Payment(BigDecimal money) {
        this.money = money;
    }

    public Payment(BigDecimal money, String paymentKey, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        this.money = money;
        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }
}
