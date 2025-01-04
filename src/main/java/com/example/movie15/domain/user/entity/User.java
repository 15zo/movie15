package com.example.movie15.domain.user.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String nickname;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private Role role;  //역할: 어드민, 유저

    private boolean isDeleted = false;

    public User(String email, String password, String nickname, String role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = Role.of(role);
    }

    public void updateIsDeleted() {
        this.isDeleted = true;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }


}
