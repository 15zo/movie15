package com.example.movie15.domain.user.entity;

import com.example.movie15.domain.review.entity.Review;
import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;  // 기본값: USER

    private boolean isDeleted = false;

    private boolean isVerified; // 이메일 인증 여부
    private String verificationToken; // 이메일 인증 토큰
    private LocalDateTime tokenExpiryTime; // 인증토큰 만료 시간

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    // 관리자 권한 확인 메소드
    public boolean hasAdminRole() {
        return this.role == Role.ADMIN;
    }

    // 회원가입용 생성자
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // 관리자 생성용 생성자
    public User(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public void updateIsDeleted() {
        this.isDeleted = true;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    // 이메일 인증여부
    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    // 인증토큰 삽입
    public void setVerificationToken(String token) {
        this.verificationToken = token;
    }

    // 인증토큰 만료시간 생성 (회원가입요청시간 + 10분)
    public void setTokenExpiryTime() {
        this.tokenExpiryTime = LocalDateTime.now().plusMinutes(10);
    }

    /**
     * 테스트용 메소드
     */
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String Name) {
        this.name = Name;
    }

}