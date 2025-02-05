package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.*;
import com.example.movie15.domain.user.service.UserService;
import com.example.movie15.global.security.JwtProvider;
import com.example.movie15.global.security.service.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto userRequestDto) throws MessagingException {
        userService.signup(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("이메일을 확인해주세요!");
    }

    // 비밀번호 변경
    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {

        Long authenticatedUserId = userDetails.getUser().getId();
        userService.updatePassword(authenticatedUserId, id, updatePasswordRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtAuthResponse authResponse = userService.login(loginRequestDto);
        return ResponseEntity.ok(authResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 탈퇴 전 비밀번호 확인
    @PostMapping("/checkPassword")
    public ResponseEntity<Boolean> checkPassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @Valid @RequestBody CheckPasswordRequestDto checkPasswordRequestDto) {

        boolean isPasswordCorrect = userService.checkPassword(userDetails.getUser().getId(), checkPasswordRequestDto.getPassword());
        return ResponseEntity.ok(isPasswordCorrect);
    }

    // 회원탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @Valid @RequestBody PasswordRequestDto passwordRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long authenticatedUserId = userDetails.getUser().getId();
        userService.deleteUser(authenticatedUserId, id, passwordRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 리프레쉬 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long authenticatedUserId = userDetails.getUser().getId();
        String refreshToken = jwtProvider.extractToken(headerValue);
        JwtAuthResponse authResponse = userService.refreshToken(authenticatedUserId, refreshToken);
        return ResponseEntity.ok(authResponse);
    }
}