package com.example.movie15.domain.user.dto;

import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
    private final Role role;
    private final boolean isVerified;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getName();
        this.role = user.getRole();
        this.isVerified = user.isVerified();
    }
}