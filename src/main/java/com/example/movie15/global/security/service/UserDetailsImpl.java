package com.example.movie15.global.security.service;

import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    // 계정의 권한 목록을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = this.user.getRole();

        return new ArrayList<>(role.getAuthorities());
    }

    // 저장된 비밀번호 값 반환
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    // 로그인 식별용 이메일 반환
    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    // 계정 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 증명 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화
    @Override
    public boolean isEnabled() {
        return true;
    }
}
