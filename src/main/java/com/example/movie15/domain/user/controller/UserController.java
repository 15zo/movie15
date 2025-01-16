package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.*;
import com.example.movie15.domain.user.service.UserService;
import com.example.movie15.global.exception.CommonResponseBody;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto userRequestDto) throws MessagingException {
        userService.signup(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("이메일을 확인해주세요!");
    }

    // 회원 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUserInfo(@PathVariable Long id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                 @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        userService.updateUserInfo(id, token, updateUserRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtAuthResponse authResponse = userService.login(loginRequestDto);
        return ResponseEntity.ok(new CommonResponseBody<>("로그인을 성공하였습니다.", authResponse));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("로그아웃을 성공하였습니다.");
    }

    // 탈퇴 전 비밀번호 확인
    @PostMapping("/{id}/check")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> checkPassword(@PathVariable Long id,
                                                                             @Valid @RequestBody CheckRequestDto checkRequestDto) {
        JwtAuthResponse authResponse = userService.checkPassword(id, checkRequestDto.getPassword());
        return ResponseEntity.ok(new CommonResponseBody<>("비밀번호를 확인하였습니다.", authResponse));
    }

    // 회원탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        userService.deleteUser(id, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴가 완료되었습니다.");
    }
}