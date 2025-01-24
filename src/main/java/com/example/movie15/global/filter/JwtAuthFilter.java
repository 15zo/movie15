package com.example.movie15.global.filter;

import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.security.AuthenticationScheme;
import com.example.movie15.global.security.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    // 화이트 리스트 경로(토큰 검증을 생략할 경로들)
    private static final String[] WHITE_LIST = {
            "/api/users/signup",
            "/api/users/login",
            "/api/user/refresh",
            "/api/error",
            "/api/verify",
            "/api/movies/**",
            "/api/cinemas/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 화이트 리스트 경로는 필터를 생략
        if (isWhiteListed(request.getRequestURI())) {
            log.info("화이트리스트 경로 요청: {} - 인증 생략", requestURI);
            filterchain.doFilter(request, response);
            return;
        }
    }

    // 요청 경로가 화이트리스트인지 확인
    private boolean isWhiteListed(String path) {
        for (String whiteListedPath : WHITE_LIST) {
            if (path.startsWith(whiteListedPath)) {
                return true;
            }
        }
        return false;
    }

    // request를 이용해 인증을 처리
    private void authenticate(HttpServletRequest request) {
        String token = this.getTokenFromRequest(request);

        // 블랙리스트 확인
        if (jwtProvider.isBlacklisted(token)) {
            log.warn("블랙리스트에 등록된 토큰입니다: {}", token);
            throw new BadValueException(ExceptionType.INVALID_TOKEN);
        }

        // 토큰 유효성 검사
        if (!StringUtils.hasText(token) || !jwtProvider.validateToken(token)) {
            log.warn("유효하지 않은 토큰입니다.");
            return;
        }

        // 사용자 인증 처리
        Long userId = jwtProvider.getUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

        setAuthentication(request, userDetails);
    }

    // 요청 헤더에서 토큰 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String headerPrefix = "Bearer ";

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix)) {
            String token = bearerToken.substring(headerPrefix.length()).trim();
            log.debug("Extracted JWT token: {}", token.substring(0, 10) + "..."); // 일부만 로깅
            return token;
        }

        log.warn("Invalid Authorization header: {}", bearerToken);
        throw new BadValueException(ExceptionType.MISSING_BEARER_TOKEN);
    }

    // SecurityContext에 인증 객체 저장
    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticaton = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        authenticaton.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticaton);
    }
}