package com.example.movie15.global.security;

import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiry-millis}")
    private long expiryMillis;

    private final RedisTemplate<String, Object> redisTemplate;


    @PostConstruct
    private void validateProperties() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY 값이 설정되지 않았거나 비어 있습니다.");
        }
        if (expiryMillis <= 0) {
            throw new IllegalStateException("jwt.expiry-millis 값은 0보다 커야 합니다.");
        }
        log.info("JWT_SECRET_KEY와 expiryMillis가 정상적으로 로드되었습니다.");
    }

    // 토큰 생성 메소드
    public String generateToken(Authentication authentication, Long userId, String purpose) {
        String username = (authentication != null) ? authentication.getName() : null;
        String role = authentication != null
                ? authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                : "ROLE_USER";

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + getExpiry(purpose));

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(currentDate)
                .expiration(expireDate)
                .claim("username", username)
                .claim("role", role)
                .claim("purpose", purpose)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 목적별 토큰 만료 시간
    private long getExpiry(String purpose) {
        return switch (purpose) {
            case "PASSWORD_CONFIRMATION" -> TimeUnit.MINUTES.toMillis(10);      // 비밀번호 확인: 10분
            case "INFO_UPDATE" -> TimeUnit.MINUTES.toMillis(15);                // 회원 정보 변경: 15분
            case "EMAIL_VERIFICATION" -> TimeUnit.MINUTES.toMillis(20);         // 이메일 인증: 20분
            case "ACCESS_TOKEN" -> expiryMillis;                                        // 액세스 토큰: application.yml의 설정 값을 사용
            case "REFRESH_TOKEN" -> TimeUnit.DAYS.toMillis(10);                 // 리프레쉬 토큰: 10일
            default -> throw new IllegalArgumentException("지원되지 않는 토큰 목적입니다: " + purpose);
        };
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token, String expectedPurpose) {
        try {
            Claims claims = parseClaims(token);
            return expectedPurpose.equals(claims.get("purpose", String.class));
        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "REFRESH_TOKEN");
    }

    private Claims parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new MalformedJwtException("토큰이 비어 있습니다.");
        }
            return Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

    public void storeRefreshToken(Long userId, String refreshToken) {
            String redisKey = "refreshToken" + userId;
            long expiryMillis = getExpiry("REFRESH_TOKEN");
            redisTemplate.opsForValue().set(redisKey, refreshToken, expiryMillis, TimeUnit.MILLISECONDS);
            log.info("리프레시 토큰 저장 완료: userId={}, token={}", userId, refreshToken);
        }

    public String getRefreshTokenFromRedis(Long userId) {
            String redisKey = "refreshToken:" + userId;
            return (String) redisTemplate.opsForValue().get(redisKey);
        }

    public void invalidateToken(String token) {
            redisTemplate.delete(token);
            log.info("토큰이 무효화됐습니다. token={}", token);
    }

    // 토큰 추출
    public String extractToken(String header) {
        log.debug("Authorization 헤더 값: '{}'", header);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            log.debug("추출된 JWT 토큰: '{}'", token);

            if (token.contains(" ")) {
                log.error("JWT 문자열에 공백이 포함돼 있습니다: '{}'", token);
                throw new MalformedJwtException("JWT 문자열에 공백이 포함돼 있습니다.");
            }
            return token;
        }
        log.error("Authorization 헤더가 올바르지 않습니다: '{}'", header);
        throw new BadValueException(ExceptionType.MISSING_BEARER_TOKEN);
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 관리자 권한 확인 메소드
    public boolean isAdmin(String token) {
        try {
            String role = getRoleFromToken(token);
            return "ROLE_ADMIN".equals(role);
        } catch (Exception e) {
            log.error("토큰에서 역할 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
