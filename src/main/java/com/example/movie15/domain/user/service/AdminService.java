package com.example.movie15.domain.user.service;

import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 유저 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 유저 권한 변경
    public void updateUserRole(Long id, String newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        user.changeRole(Role.valueOf(newRole.toUpperCase()));
    }

    // 유저 삭제
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        user.updateIsDeleted();

        userRepository.save(user);
    }
}