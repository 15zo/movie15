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
            "/api/mvoies/**",
            "/api/cinemas/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {

        // 화이트 리스트 경로는 필터를 생략
        if (isWhiteListed(request.getRequestURI())) {
            filterchain.doFilter(request, response);

            return;
        }

        this.authenticate(request);
        filterchain.doFilter(request, response);
    }

    // 화이트 리스트 경로 확인
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

        if (!StringUtils.hasText(token) || !jwtProvider.validateToken(token, "ACCESS_TOKEN")) {
            return;
        }

        String username = this.jwtProvider.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        setAuthentication(request, userDetails);
    }

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



    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        // 찾아온 사용자 정보로 인증 객체를 생성
        UsernamePasswordAuthenticationToken authenticaton = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        authenticaton.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authenticaton);
    }
}