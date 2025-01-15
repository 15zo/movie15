package com.example.movie15.domain.user.service;

import com.example.movie15.domain.email.service.SignupEmailSenderService;
import com.example.movie15.domain.user.dto.JwtAuthResponse;
import com.example.movie15.domain.user.dto.LoginRequestDto;
import com.example.movie15.domain.user.dto.UserRequestDto;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private SignupEmailSenderService emailSenderService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 요청이 유효하면 사용자 저장하고 이메일 인증 발송")
    void testSignup_WithValidRequest_ShouldSaveUserAndSendEmail() throws Exception {
        // Given: 회원가입 요청에 대한 DTO와 이미 존재하는 이메일을 확인
        UserRequestDto userRequestDto = new UserRequestDto("test@example.com", "password123");
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(emailSenderService.sendVerificationEmail(anyString())).thenReturn("mock-token");

        // When: 회원가입 메서드 호출
        userService.signup(userRequestDto);

        // Then: 사용자 저장 및 이메일 발송이 일어났는지 검증
        verify(userRepository).save(any(User.class));
        verify(emailSenderService).sendVerificationEmail(userRequestDto.getEmail());
    }

    @Test
    @DisplayName("올바른 자격증명으로 로그인 시 JWT 토큰을 반환")
    void testLogin_WithValidCredentials_ShouldReturnJwtAuthResponse() {
        // Given: 유효한 로그인 요청 DTO와 mock 사용자 정보 설정
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password123");
        User mockUser = new User("test@example.com", "encoded-password", "김명호");
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtProvider.generateToken(authentication, mockUser.getId())).thenReturn("mock-jwt-token");

        // When: 로그인 메서드 호출
        JwtAuthResponse response = userService.login(loginRequestDto);

        // Then: JWT 토큰이 정상적으로 반환되었는지 검증
        assertNotNull(response);
        assertEquals("Bearer", response.getTokenAuthScheme());
        assertEquals("mock-jwt-token", response.getAccessToken());
    }

    @Test
    @DisplayName("올바른 비밀번호 입력 시 임시 JWT 토큰 반환")
    void testCheckPassword_WithCorrectPassword_ShouldReturnTempToken() {
        // Given: 비밀번호 확인을 위한 mock 사용자 설정
        User mockUser = new User("test@example.com", "encoded-password", "김명호");
        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(mockUser);
        when(passwordEncoder.matches("correct-password", mockUser.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtProvider.tempToken(authentication)).thenReturn("temp-jwt-token");

        // When: 비밀번호 확인 메서드 호출
        JwtAuthResponse response = userService.checkPassword(1L, "correct-password");

        // Then: 임시 JWT 토큰이 반환되었는지 검증
        assertNotNull(response);
        assertEquals("Bearer", response.getTokenAuthScheme());
        assertEquals("temp-jwt-token", response.getAccessToken());
    }

    @Test
    @DisplayName("유효한 토큰으로 로그아웃 시 토큰 무효화")
    void testLogout_WithValidToken_ShouldInvalidateToken() {
        // Given: 유효한 토큰과 인증된 사용자 설정
        String validToken = "valid-jwt-token";
        String username = "test@example.com";

        // 인증 정보를 설정하여 SecurityContext에 인증된 사용자 정보 추가
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);  // Context에 인증 정보 설정

        // `validToken` 메서드가 true를 반환하도록 설정
        when(jwtProvider.validToken(validToken)).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // `invalidateToken` 메서드를 호출하도록 설정
        doNothing().when(jwtProvider).invalidateToken(validToken);

        // When: 로그아웃 메서드 호출
        userService.logout(request);

        // Then: 토큰이 무효화된 것을 검증
        verify(jwtProvider).invalidateToken(validToken);  // invalidateToken 호출 검증
        verify(authentication).getPrincipal();  // 인증된 사용자 정보 검증
    }
}