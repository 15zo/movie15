package com.example.movie15.domain.user;

import com.example.movie15.domain.user.entity.Role;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeAdmin() {
        String adminEmail = "admin@naver.com";
        String adminPassword = "Asdf1234!";
        String adminNickname = "Admin";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            // 생성자를 사용해 관리자 계정을 생성
            User admin = new User(
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    adminNickname,
                    Role.ADMIN
            );
            userRepository.save(admin);
            log.info("기본 관리자 계정이 생성되었습니다: " + adminEmail);
        } else {
            log.info("관리자 계정이 이미 존재합니다.");
        }
    }
}
