package com.example.movie15.domain.user.service;

import com.example.movie15.domain.email.service.SignupEmailSenderService;
import com.example.movie15.domain.rabbitmq.producer.RabbitUserSignupProducer;
import com.example.movie15.domain.user.dto.JwtAuthResponse;
import com.example.movie15.domain.user.dto.LoginRequestDto;
import com.example.movie15.domain.user.dto.UpdateUserRequestDto;
import com.example.movie15.domain.user.dto.UserRequestDto;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.ForbiddenException;
import com.example.movie15.global.exception.WrongAccessException;
import com.example.movie15.global.security.AuthenticationScheme;
import com.example.movie15.global.security.JwtProvider;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RabbitUserSignupProducer rabbitUserSignupProducer;
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
        User user = new User(userRequestDto.getEmail(), encodedPassword,userRequestDto.getName());

        // <<초기값: 인증 미완료 상태.>> 인증안하고 로그인 시도시 이 변수값으로 로그인 못하게하면됨.
        user.setVerified(false);

        // <<이메일 보내기>> 이메일 인증 토큰 생성 및 설정.
        String token = emailSenderService.sendVerificationEmail(user.getEmail()); // 이메일 전송 후 토큰반환받음.
        user.setVerificationToken(token); // 발급된 토큰 user 에 설정
        user.setTokenExpiryTime(); // 토큰 유효시간: 10분

        userRepository.save(user);

        rabbitUserSignupProducer.userSignupEvent(user.getId(), user.getTokenExpiryTime()); // rabbitmq
    }

    @Transactional
    public void updateUserInfo(Long userId, String token, UpdateUserRequestDto updateUserRequestDto) {
        Long requesterId = jwtProvider.getUserId(token);

        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        User user = userRepository.findByIdOrElseThrow(userId);

        if (updateUserRequestDto.getNewPassword() != null) {
            validatePasswordChange(user, updateUserRequestDto);
            user.updatePassword(passwordEncoder.encode(updateUserRequestDto.getNewPassword()));
        }

        userRepository.save(user);
    }

    public JwtAuthResponse login(LoginRequestDto loginRequestDto) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new WrongAccessException(ExceptionType.WRONG_EMAIL));

        // 비밀번호 검증 및 삭제된 사용자 검증(보안을 위해 로직을 통합했음)
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()) || user.isDeleted()) {
            throw new WrongAccessException(ExceptionType.WRONG_PASSWORD);
        }

        // 회원가입 이메일 인증을 하지 않았을때
        if (!user.isVerified()) {
            throw new ForbiddenException(ExceptionType.EMAIL_NOT_VERIFIED);
        }

        // 사용자 인증 후 인증 객체를 저장
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateToken(authentication, user.getId());
        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), accessToken);
    }

    // 회원 탈퇴 시 비밀번호 확인
    public JwtAuthResponse checkPassword(Long userId, String password) {
        User user = userRepository.findByIdOrElseThrow(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongAccessException(ExceptionType.WRONG_PASSWORD);
        }

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), password)
        );

        String tempToken = this.jwtProvider.tempToken(authentication);
        return new JwtAuthResponse(AuthenticationScheme.BEARER.getName(), tempToken);
    }

    @Transactional
    public void deleteUser(Long userId, String token) {
        Long requesterId = jwtProvider.getUserId(token);

        if (!userId.equals(requesterId)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }

        User user = userRepository.findByIdOrElseThrow(userId);
        user.updateIsDeleted();
        userRepository.save(user);
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (!jwtProvider.validToken(token)) {
            throw new WrongAccessException(ExceptionType.WRONG_TOKEN);
        }
        jwtProvider.invalidateToken(token);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadValueException(ExceptionType.MISSING_BEARER_TOKEN);
        }
        return jwtProvider.extractToken(authorizationHeader);
    }

    private void validatePasswordChange(User user, UpdateUserRequestDto dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new WrongAccessException(ExceptionType.INVALID_CURRENT_PASSWORD);
        }

        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new BadValueException(ExceptionType.PASSWORD_SAME_AS_PREVIOUS);
        }
    }
}