package com.example.movie15.global.filter;

import com.example.movie15.global.security.AuthenticationScheme;
import com.example.movie15.global.security.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        if (!StringUtils.hasText(token) || !jwtProvider.validToken(token)) {
            return;
        }

        String username = this.jwtProvider.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        setAuthentication(request, userDetails);
    }

    // request의 Authorization 헤더에서 토큰 값을 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String headerPrefix = AuthenticationScheme.generateType(AuthenticationScheme.BEARER);
        boolean tokenFound = StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix);

        if (tokenFound) {
            return bearerToken.substring(headerPrefix.length());
        }
        return null;
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