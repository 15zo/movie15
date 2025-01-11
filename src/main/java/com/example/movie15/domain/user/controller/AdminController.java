package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.UserResponseDto;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.movie15.domain.user.entity.QUser.user;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 모든 유저 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 유저 상세 조회
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long id) {
        UserResponseDto user = adminService.getUserDetails(id);
        return ResponseEntity.ok(user);
    }

    // 유저 권한 변경
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        adminService.updateUserRole(id, role);
        return ResponseEntity.ok().build();
    }

    // 유저 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
