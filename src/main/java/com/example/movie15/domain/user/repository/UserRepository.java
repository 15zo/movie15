package com.example.movie15.domain.user.repository;

import com.example.movie15.domain.user.entity.User;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));
    }

    List<User> findAllByEmailIn(List<String> emails);


    // 인증 토큰으로 사용자 검색
    User findByVerificationToken(String token);

    // "시간이 만료된 토큰 and 인증상태가 false 인" 사용자 조회
    @Query("SELECT u FROM User u WHERE u.tokenExpiryTime < :now AND u.isVerified = false")
    List<User> findUsersWithExpiredTokens(LocalDateTime now);
}

