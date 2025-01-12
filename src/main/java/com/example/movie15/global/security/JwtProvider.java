package com.example.movie15.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiry-millis:600}")  // 토큰 만료 시간: 30분
    private long expiryMillis;

    private final RedisTemplate<String, Object> redisTemplate;

    // 1. 설정 및 초기화 관련 메소드
    // secret과 expriyMillis 유효성 검증
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

    // 2. 토큰 생성 메소드
    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("권한이 없습니다."));

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + this.expiryMillis);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .claim("role", role)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 임시 토큰 생성 -> 이메일 인증, 탈퇴 전 비밀번호 확인, 회원정보변경(비밀번호변경)에서 사용됨.
    public String tempToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + 60000);  // 임시 토큰 만료 시간: 1분

        return Jwts.builder()
                .subject(username)
                .claim("type", "temp")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰 유효성 및 무효화 관련 메소드
    // 토큰 유효성 검사(무효화된 토큰 포함)
    public boolean validToken(String token) throws JwtException {
        if (isTokenInvalidated(token)) {
            log.error("무효화된 토큰입니다. token: {}", token);
            return false;
        }
        try {
            return !this.tokenExpired(token);
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 토큰입니다.: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료됐습니다.: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
        }
        return false;
    }

    // 토큰이 무효화됐는지 확인
    public boolean isTokenInvalidated(String token) {
        return redisTemplate.hasKey(token);
    }

    // 토큰 무효화
    public void invalidateToken(String token) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();

        // Redis에 토큰 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set(token, "invalid", expiration.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        log.info("토큰이 무효화됐습니다. token: {}, 만료 시간: {}", token, expiration);
    }

    // 4. JWT 클레임 및 속성 처리 메소드
    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        Claims claims = this.getClaims(token);
        return claims.getSubject();
    }

    // 임시 토큰 여부 확인
    public boolean isTempToken(String token) {
        Claims claims = getClaims(token);
        System.out.println(claims.get("type"));
        return "temp".equals(claims.get("type"));
    }

    // JWT의 claim 부분을 추출
    private Claims getClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new MalformedJwtException("토큰이 비어 있습니다.");
        }
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    // 입력 받은 토큰의 만료 여부 확인
    private boolean tokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 입력 받은 토큰의 만료일 반환
    private Date getExpirationDateFromToken(String token) {

        return this.resolveClaims(token, Claims::getExpiration);
    }

    // 토큰에 입력 받은 로직 적용 및 결과 반환
    private <T> T resolveClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.getClaims(token);
        return claimsResolver.apply(claims);
    }
}