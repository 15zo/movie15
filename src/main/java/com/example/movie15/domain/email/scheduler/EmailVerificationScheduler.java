package com.example.movie15.domain.email.scheduler;

import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
// 1분마다 만료된유저 지우는 클래스
public class EmailVerificationScheduler { // 자동으로 호출되는 클래스. @Scheduled 로 시간설정.

    private final UserRepository userRepository;

    // 일정 주기로 만료된 인증 토큰 정리
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void cleanUpExpiredTokens() {
        // 현재 시간 기준으로 만료된 유저 데이터 조회
        List<User> expiredUsers = userRepository.findUsersWithExpiredTokens(LocalDateTime.now());

        // 만료된 유저 데이터를 삭제 (이메일인증을 하지 않은 유저의 정보를 삭제)
        userRepository.deleteAll(expiredUsers);
    }
}
