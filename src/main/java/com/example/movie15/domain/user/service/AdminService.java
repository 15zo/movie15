package com.example.movie15.domain.user.service;

import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.movie15.domain.user.entity.QUser.user;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 유저 조회
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        return users.stream()
                .map(UserResponseDto::new)
                .toList();
    }

    // 유저 상세 조회
    public UserResponseDto getUserDetails(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserRole = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse(null);

        if (!"ROLE_ADMIN".equals(currentUserRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
        }
        return new UserResponseDto(user);
    }

    // 유저 권한 변경
    public void updateUserRole(Long id, String newRole) {
        if (!hasAdminRole()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        User user = userRepository.findById(id).orElseThrow(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
        }

        if (newRole == null || newRole.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다.");
        }

        boolean isValidRole = false;
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(newRole)) {
                isValidRole = true;
                break;
            }
        }

        if (!isValidRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다.");
        }

        Role role = Role.valueOf(newRole.toUpperCase());
        user.changeRole(role);
    }

    // 유저 삭제
    public void deleteUser(Long id) {

        if (!hasAdminRole()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        User user = userRepository.findById(id).orElseThrow(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
        }

        user.updateIsDeleted();
        userRepository.save(user);
    }

    // 관리자 권한 확인
    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
    }
}
