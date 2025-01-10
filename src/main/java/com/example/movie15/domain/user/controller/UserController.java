package com.example.movie15.domain.user.controller;

import com.example.movie15.domain.user.dto.*;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.domain.user.service.UserService;
import com.example.movie15.global.exception.CommonResponseBody;
import com.example.movie15.global.security.service.UserDetailsImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto userRequestDto) throws MessagingException {
        userService.signup(userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("이메일을 확인해주세요!");
    }

    // 회원 정보 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUserInfo(@PathVariable Long userId,
                                                 @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        userService.updateUserInfo(userId, updateUserRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtAuthResponse authResponse = userService.login(loginRequestDto);

        return ResponseEntity.ok(new CommonResponseBody<>("로그인을 성공하였습니다.", authResponse));
    }

    // 탈퇴 전 비밀번호 확인
    @PostMapping("/{userId}/check")
    public ResponseEntity<CommonResponseBody<JwtAuthResponse>> checkPassword(@PathVariable Long userId,
                                                                             @Valid @RequestBody CheckRequestDto checkRequestDto) {
        JwtAuthResponse authResponse = userService.checkPassword(userId, checkRequestDto.getPassword());

        return ResponseEntity.ok(new CommonResponseBody<>("비밀번호를 확인하였습니다.", authResponse));
    }

    // 회원탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String tempToken) {
        String extractToken = userService.extractToken(tempToken);
        boolean checked = userService.checkHeader(extractToken);

        if (checked) {
            userService.deleteUser(userId);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴가 완료되었습니다.");
    }
}
