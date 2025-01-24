package com.example.movie15.domain.user.service;

import com.example.movie15.domain.email.service.SignupEmailSenderService;
import com.example.movie15.domain.user.dto.JwtAuthResponse;
import com.example.movie15.domain.user.dto.LoginRequestDto;
import com.example.movie15.domain.user.dto.UpdateUserRequestDto;
import com.example.movie15.domain.user.dto.UserRequestDto;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.*;
import com.example.movie15.global.security.AuthenticationScheme;
import com.example.movie15.global.security.JwtProvider;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final SignupEmailSenderService emailSenderService;

    @Transactional
    public void signup(UserRequestDto userRequestDto) throws MessagingException {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new BadValueException(ExceptionType.EXIST_USER);
        }

        // 암호화된 비밀번호를 엔티티 생성 시 바로 설정
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        User user = new User(userRequestDto.getEmail(), encodedPassword, userRequestDto.getName());

        // <<초기값: 인증 미완료 상태.>> 인증안하고 로그인 시도시 이 변수값으로 로그인 못하게하면됨.
        user.setVerified(false);

        // <<이메일 보내기>> 이메일 인증 토큰 생성 및 설정.
        String token = emailSenderService.sendVerificationEmail(user.getEmail()); // 이메일 전송 후 토큰반환받음.
        user.setVerificationToken(token); // 발급된 토큰 user 에 설정
        user.setTokenExpiryTime(); // 토큰 유효시간: 10분

        userRepository.save(user);
    }

    @Transactional
    public void updateUserInfo(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (updateUserRequestDto.getNewPassword() != null) {
            validatePasswordChange(user, updateUserRequestDto);
            user.updatePassword(passwordEncoder.encode(updateUserRequestDto.getNewPassword()));
        }

        userRepository.save(user);
    }

    public JwtAuthResponse login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new WrongAccessException(ExceptionType.WRONG_EMAIL));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()) || user.isDeleted()) {
            throw new WrongAccessException(ExceptionType.WRONG_PASSWORD);
        }

        // 액세스 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getId());

        // 리프레시 토큰 생성  및 Redis 저장
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        jwtProvider.storeRefreshToken(user.getId(), refreshToken);

        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), accessToken, refreshToken);
    }

    // 회원 탈퇴 시 비밀번호 확인
    public void checkPassword(Long userId, String password) {
        User user = userRepository.findByIdOrElseThrow(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongAccessException(ExceptionType.WRONG_PASSWORD);
        }
    }

    @Transactional
    public void deleteUser(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ForbiddenException(ExceptionType.WRONG_PASSWORD);
        }

        userRepository.delete(user);
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        // 토큰 검증
        if (!jwtProvider.validateToken(token)) {
            throw new WrongAccessException(ExceptionType.WRONG_TOKEN);
        }

        // 토큰을 블랙리스트에 등록
        jwtProvider.blacklistToken(token);
        log.info("로그아웃된 토큰이 블랙리스트에 등록됐습니다: {}", token);
    }

    public JwtAuthResponse refreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadValueException(ExceptionType.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        if (!jwtProvider.validateStoredRefreshToken(userId, refreshToken)) {
            throw new BadValueException(ExceptionType.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        jwtProvider.storeRefreshToken(userId, newRefreshToken);

        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), newAccessToken, newRefreshToken);
    }

    private void validatePasswordChange(User user, UpdateUserRequestDto dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new WrongAccessException(ExceptionType.INVALID_CURRENT_PASSWORD);
        }

        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new BadValueException(ExceptionType.PASSWORD_SAME_AS_PREVIOUS);
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadValueException(ExceptionType.MISSING_BEARER_TOKEN);
        }
        return jwtProvider.extractToken(authorizationHeader);
    }
}