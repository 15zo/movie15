package com.example.movie15.domain.inquiry.entity;

import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String content;
    private InquiryStatus status; // PENDING, ANSWERED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
