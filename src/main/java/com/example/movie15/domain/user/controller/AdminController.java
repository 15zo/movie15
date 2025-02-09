package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.UpdateRoleRequestDto;
import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 모든 유저 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 유저 상세 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        UserResponseDto user = adminService.getUserDetails(userId);
        return ResponseEntity.ok(user);
    }

    // 유저 권한 변경
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId,
                                                 @Valid @RequestBody UpdateRoleRequestDto updateRoleRequestDto) {
        adminService.updateUserRole(userId, updateRoleRequestDto.getRole());
        return ResponseEntity.ok("권한이 성공적으로 변경됐습니다.");
    }

    // 유저 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}