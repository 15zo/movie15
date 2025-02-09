package com.example.movie15.domain.user.service;

import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.NotFoundException;
import com.example.movie15.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    // 모든 유저 조회
    public List<UserResponseDto> getAllUsers() {


        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponseDto::new).toList();
    }

    // 유저 상세 조회
    public UserResponseDto getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));
        return new UserResponseDto(user);
    }

    // 유저 권한 변경
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BadValueException(ExceptionType.ALREADY_DELETED_USER);
        }

        try {
            Role role = Role.valueOf(newRole.toUpperCase());

            if (user.getRole() == role) {
                throw new BadValueException(ExceptionType.ALREADY_SAME_ROLE);
            }

            user.changeRole(role);
        } catch (IllegalArgumentException e) {
            throw new BadValueException(ExceptionType.INVALID_USER_ROLE);
        }
    }

    // 유저 삭제
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BadValueException(ExceptionType.ALREADY_DELETED_USER);
        }

        user.updateIsDeleted();
    }
}
