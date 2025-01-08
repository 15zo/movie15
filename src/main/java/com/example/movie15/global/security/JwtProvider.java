package com.example.movie15.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiry-millis:600}")
    private long expiryMillis;

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

    // 임시 토큰 생성
    public String tempToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + 60000);

        return Jwts.builder()
                .subject(username)
                .claim("type", "temp")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        Claims claims = this.getClaims(token);
        return claims.getSubject();
    }

    // 토큰 유효성 검사
    public boolean validToken(String token) throws JwtException {
        try {
            return !this.tokenExpired(token);
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 토큰입니다.: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT 토큰이 만료됐습니다.: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
        }
        return false;
    }

    // 임시 토큰 검증
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

    // 입력 받은 토큰의 만료 여부
    private boolean tokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 입력 받은 토큰의 만료일을 반환
    private Date getExpirationDateFromToken(String token) {

        return this.resolveClaims(token, Claims::getExpiration);
    }

    // 토큰에 입력 받은 로직을 적용하고 그 결과를 반환
    private <T> T resolveClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.getClaims(token);
        return claimsResolver.apply(claims);
    }
}