package com.example.movie15.domain.user.entity;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public enum Role {
    USER("user"),
    ADMIN("admin");

    private final String name;

    Role(String name) {

        this.name = name;
    }

    public static Role of(String roleName) {
        for (Role role : values()) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }

        throw new IllegalArgumentException("해당하는 이름의 권한을 찾을 수 없습니다: " + roleName);
    }

    // 권한 리스트
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}
