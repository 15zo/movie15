package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.*;
import com.example.movie15.domain.user.service.UserService;
import com.example.movie15.global.exception.CommonResponseBody;
import com.example.movie15.global.security.JwtProvider;
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
    private final JwtProvider jwtProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto userRequestDto) throws MessagingException {
        userService.signup(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("이메일을 확인해주세요!");
    }

    // 회원 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUserInfo(@PathVariable Long id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
                                                 @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {

        String token = jwtProvider.extractToken(headerValue);
        Long userId = jwtProvider.getUserId(token);
        userService.updateUserInfo(userId, updateUserRequestDto);
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
        userService.checkPassword(id, checkRequestDto.getPassword());
        return ResponseEntity.ok(new CommonResponseBody<>("비밀번호를 확인했습니다."));
    }

    // 회원탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
                                             @RequestParam String password) {
        String token = jwtProvider.extractToken(headerValue);
        Long userId = jwtProvider.getUserId(token);
        userService.deleteUser(userId, password);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴가 완료되었습니다.");
    }

    // 리프레쉬 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue) {
        String refreshToken = jwtProvider.extractToken(headerValue);
        JwtAuthResponse authResponse = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(new CommonResponseBody<>("리프레쉬 토큰이 갱신됐습니다.", authResponse));
    }
}