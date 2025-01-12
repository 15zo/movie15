package com.example.movie15.domain.inquiry.entity;

import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String subject;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.PENDING; // 기본값: PENDING(답변 대기 중)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Inquiry(String subject, String content, User user) {
        this.subject = subject;
        this.content = content;
        this.user = user;
    }

    public void update(String subject, String content) {
        if (this.status == InquiryStatus.ANSWERED) {
            throw new IllegalStateException("이미 답변된 문의는 수정할 수 없습니다.");
        }
        this.subject = subject;
        this.content = content;
    }
}
