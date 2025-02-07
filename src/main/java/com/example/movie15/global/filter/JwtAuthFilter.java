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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    // 화이트 리스트 경로(토큰 검증을 생략할 경로들)
    private static final AntPathRequestMatcher[] WHITE_LIST = {
        new AntPathRequestMatcher("/api/users/signup"),
        new AntPathRequestMatcher("/api/users/login"),
        new AntPathRequestMatcher("/api/users/refresh"),
        new AntPathRequestMatcher("/api/error"),
        new AntPathRequestMatcher("/api/verify"),
        new AntPathRequestMatcher("/api/movies"),
        new AntPathRequestMatcher("/api/movies/search"),
        new AntPathRequestMatcher("/api/movies/playing"),
        new AntPathRequestMatcher("/api/cinemas/**"),
        new AntPathRequestMatcher("/api/payment/**"),
        new AntPathRequestMatcher("/api/booking/**"),
        new AntPathRequestMatcher("api/runtimes"),
        new AntPathRequestMatcher("api/runtimes/**"),
        new AntPathRequestMatcher("/api/movies/{movieId}", HttpMethod.GET.toString())
    };

    private static final AntPathRequestMatcher[] BLACK_LIST = {
        new AntPathRequestMatcher("/api/movies/popular", HttpMethod.GET.toString())
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 화이트 리스트 경로는 필터를 생략
        if (isWhiteListed(request)) {
            log.info("화이트리스트 경로 요청: {} - 인증 생략", requestURI);
            filterchain.doFilter(request, response);
            return;
        }

        try {
            // 화이트 리스트에 해당하지 않는 요청에 대한 인증 처리
            authenticate(request);
            filterchain.doFilter(request, response);
        } catch (BadValueException e) {
            // 인증 실패 처리
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"인증이 필요합니다 - 토큰필요\"}");
        }
    }

    // 요청 경로가 화이트리스트인지 확인
    private static boolean isWhiteListed(HttpServletRequest request) {
        for (AntPathRequestMatcher whiteListedPath : WHITE_LIST) {
            for (AntPathRequestMatcher blackListedPath : BLACK_LIST) {
                if (whiteListedPath.matches(request) && !blackListedPath.matches(request)) {
                    return true;
                }
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
            throw new BadValueException(ExceptionType.BLACKLISTED_TOKEN);
        }

        // 토큰 검증
        if (!jwtProvider.validateToken(token)) {
            log.warn("유효하지 않은 토큰입니다.");
            throw new BadValueException(ExceptionType.INVALID_TOKEN);
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
            log.debug("추출된 JWT 토큰: {}", token.substring(0, 10) + "..."); // 일부만 로깅
            return token;
        }

        log.warn("유효하지 않은 Authorization header: {}", bearerToken);
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