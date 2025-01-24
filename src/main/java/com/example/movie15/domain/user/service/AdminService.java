package com.example.movie15.domain.user.service;

import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.ForbiddenException;
import com.example.movie15.global.exception.NotFoundException;
import com.example.movie15.global.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.movie15.domain.user.entity.QUser.user;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    // 모든 유저 조회
    public List<UserResponseDto> getAllUsers(String token) {
        validateAdmin(token);
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::new)
                .toList();  // 유저가 없으면 빈 리스트 반환
    }

    // 유저 상세 조회
    public UserResponseDto getUserDetails(Long userId, String token) {
        validateAdmin(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));
        return new UserResponseDto(user);
    }

    // 유저 권한 변경
    public void updateUserRole(Long userId, String newRole, String token) {
        validateAdmin(token);
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        // newRole 값 검증
        if (newRole == null || newRole.isBlank()) {
            throw new BadValueException(ExceptionType.NOT_BLANK);
        }

        // 역할 검증
        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadValueException(ExceptionType.INVALID_USER_ROLE);
        }

        user.changeRole(role);
    }

    // 유저 삭제
    public void deleteUser(Long userId, String token) {
        validateAdmin(token);

        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        user.updateIsDeleted();
        userRepository.save(user);
    }

    // JWT를 통한 토큰 유효성 검증 및 관리자 권한 검증
    private void validateAdmin(String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        if (!jwtProvider.isAdmin(token)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }
    }
}
