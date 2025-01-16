package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 모든 유저 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        List<UserResponseDto> users = adminService.getAllUsers(token);
        return ResponseEntity.ok(users);
    }

    // 유저 상세 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        UserResponseDto user = adminService.getUserDetails(userId, token);
        return ResponseEntity.ok(user);
    }

    // 유저 권한 변경
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestParam String role, @RequestHeader("Authorization") String token) {
        adminService.updateUserRole(userId, role, token);
        return ResponseEntity.ok().build();
    }

    // 유저 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        adminService.deleteUser(userId, token);
        return ResponseEntity.noContent().build();
    }
}