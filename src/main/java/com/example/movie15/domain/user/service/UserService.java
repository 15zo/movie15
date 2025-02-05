package com.example.movie15.domain.user.service;

import com.example.movie15.domain.email.service.SignupEmailSenderService;
import com.example.movie15.domain.rabbitmq.producer.RabbitUserSignupProducer;
import com.example.movie15.domain.user.dto.JwtAuthResponse;
import com.example.movie15.domain.user.dto.LoginRequestDto;
import com.example.movie15.domain.user.dto.UpdatePasswordRequestDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RabbitUserSignupProducer rabbitUserSignupProducer;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SignupEmailSenderService emailSenderService;

    @Transactional
    public void signup(UserRequestDto userRequestDto) throws MessagingException {
        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());

        if (existingUser.isPresent()) {
            if (existingUser.get().isDeleted()) {
                throw new BadValueException(ExceptionType.DELETED_EMAIL_REUSE);
            }
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
        user.setTokenExpiryTime(10); // 토큰 유효시간: 10분

        userRepository.save(user);

        rabbitUserSignupProducer.userSignupEvent(user.getId(), user.getTokenExpiryTime()); // rabbitmq
    }

    @Transactional
    public void updatePassword(Long authenticatedUserId, Long userId, UpdatePasswordRequestDto updatePasswordRequestDto) {
        if (!authenticatedUserId.equals(userId)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        validatePasswordChange(user, updatePasswordRequestDto);
        user.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
    }

    public JwtAuthResponse login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmailAndIsDeletedFalse(loginRequestDto.getEmail())
                .orElseThrow(() -> new WrongAccessException(ExceptionType.WRONG_EMAIL));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new WrongAccessException(ExceptionType.WRONG_PASSWORD);
        }

        // 회원가입 이메일 인증을 하지 않았을때
        if (!user.isVerified()) {
            throw new ForbiddenException(ExceptionType.EMAIL_NOT_VERIFIED);
        }

        if (user.isDeleted()) {
            throw new WrongAccessException(ExceptionType.WRONG_EMAIL);
        }

        String role = user.getRole().name();

        // 액세스 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getId(), role);

        // 리프레시 토큰 생성  및 Redis 저장
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), role);
        jwtProvider.storeRefreshToken(user.getId(), refreshToken);

        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), accessToken, refreshToken);
    }

    public boolean checkPassword(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BadValueException(ExceptionType.ALREADY_DELETED_USER);
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public void deleteUser(Long authenticatedUserId, Long userId, String password) {
        log.info("인증된 사용자 ID: {}", authenticatedUserId);
        log.info("요청된 탈퇴 대상 사용자 ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        if (!authenticatedUserId.equals(userId)) {
            log.info("인증된 사용자와 요청된 사용자 ID 불일치: {} != {}", authenticatedUserId, userId);
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        if (user.isDeleted()) {
            log.info("이미 탈퇴된 사용자입니다: {}", userId);
            throw new BadValueException(ExceptionType.ALREADY_DELETED_USER);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.info("비밀번호 불일치. 입력된 비밀번호: {}", password);
            throw new ForbiddenException(ExceptionType.WRONG_PASSWORD);
        }

        log.info("회원 탈퇴 성공: {}", userId);
        user.updateIsDeleted();
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

    public JwtAuthResponse refreshToken(Long authenticatedUserId, String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadValueException(ExceptionType.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserId(refreshToken);

        if (!authenticatedUserId.equals(userId)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        if (!jwtProvider.validateStoredRefreshToken(userId, refreshToken)) {
            throw new BadValueException(ExceptionType.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        String role = user.getRole().name();

        String newAccessToken = jwtProvider.generateAccessToken(userId, role);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId, role);

        jwtProvider.storeRefreshToken(userId, newRefreshToken);

        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), newAccessToken, newRefreshToken);
    }

    private void validatePasswordChange(User user, UpdatePasswordRequestDto dto) {
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