package com.example.movie15.domain.user.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

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

    public void updateName(String newName) {
        this.name = newName;
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

}
