package com.example.movie15.domain.email.controller;

import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VerificationController { // """사용자가 이메일 인증을 클릭했을때""", 동작하는 클래스

    private final UserRepository userRepository;

    // 사용자가 이메일인증을 했을때 동작하는 메소드
    @GetMapping("/api/verify") // 사용자가 클릭한 인증 링크에서 호출
    public String verifyEmail(@RequestParam String token) {
        // 토큰 검증 및 결과 반환
        boolean isVerified = verifyUser(token);
        return isVerified ? "이메일 인증성공!" : "유효하지 않거나 만료된 토큰입니다.";
    }

    // 이메일 인증 처리 -> 사용자가 이메일로 링크를 클릭했을때 동작
    public boolean verifyUser(String token) {
        // 토큰으로 유저 검색
        User user = userRepository.findByVerificationToken(token);
        if (user != null && LocalDateTime.now().isBefore(user.getTokenExpiryTime())) {
            user.setVerified(true); // 인증 상태로 변경
            user.setVerificationToken(null); // 토큰 삭제
            userRepository.save(user); // """""""""""업데이트된 사용자 저장"""""""""""
            return true; // 인증 성공
        }
        return false; // 인증 실패
    }
}
