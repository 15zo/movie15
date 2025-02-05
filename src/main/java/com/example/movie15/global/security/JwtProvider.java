package com.example.movie15.global.security;

import com.example.movie15.global.exception.ExceptionType;
import com.example.movie15.global.exception.ForbiddenException;
import com.example.movie15.global.exception.WrongAccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiry-millis}")
    private long accessExpiryMillis;

    @Value("${jwt.refresh-expiry-millis}")
    private long refreshExpiryMillis;

    private final RedisTemplate<String, Object> redisTemplate;


    @PostConstruct
    private void validateProperties() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY 값이 설정되지 않았거나 비어 있습니다.");
        }
        if (accessExpiryMillis <= 0 || refreshExpiryMillis <= 0) {
            throw new IllegalStateException("토큰 만료 시간이 0보다 커야 합니다.");
        }
        log.info("JWT_SECRET_KEY와 토큰 만료 시간 설정이 정상적으로 로드되었습니다.");
    }

    // 토큰 생성
    public String generateToken(Long userId, String role, long expiryMillis) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiryMillis);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, accessExpiryMillis);
    }

    public String generateRefreshToken(Long userId, String role) {
        return generateToken(userId, role, refreshExpiryMillis);
    }

    // Authorization 헤더에서 토큰 추출
    public String extractToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        } else {
            throw new IllegalArgumentException("유효하지 않은 Authorization 헤더 값입니다.");
        }
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            log.info("토큰 검증 시작: {}", token);
            if (isBlacklisted(token)) {
                log.warn("블랙리스트에 등록된 토큰: {}", token);
                throw new WrongAccessException(ExceptionType.BLACKLISTED_TOKEN);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                log.warn("만료된 토큰: {}", token);
                throw new WrongAccessException(ExceptionType.EXPIRED_TOKEN);
            }

            log.info("토큰 검증 성공: {}", token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }

    // 토큰의 사용자 ID 추출
    public Long getUserId(String token) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // 토큰을 통해 Admin 권한이 있는지 검증
    public boolean hasAdminRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException(ExceptionType.FORBIDDEN_ACTION);
        }
        return true;
    }

    // 리프레시 토큰 저장(Redis)
    public void storeRefreshToken(Long userId, String refreshToken) {
        String rediskey = "refreshToken:" + userId;
        redisTemplate.opsForValue().set(rediskey, refreshToken, refreshExpiryMillis, TimeUnit.MILLISECONDS);
        log.info("리프레시 토큰 저장 완료: userId={}, token 끝자리 ={}", userId, refreshToken.substring(refreshToken.length() - 5));
    }

    // 리프레시 토큰 검증
    public boolean validateStoredRefreshToken(Long userId, String refreshToken) {
        String redisKey = "refreshToken:" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        return refreshToken.equals(storedToken);
    }

    // Redis 키의 TTL 확인(리프레시 토큰, 블랙리스트 토큰 만료 시간)
    public Long getKeyTTL(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl == null || ttl < 0) {
            log.warn("Redis 키의 TTL이 설정되지 않았거나 만료됐습니다: {}", key);
        }
        return ttl;
    }

    // Redis 키 삭제
    public void deleteKey(String key) {
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Redis 키 삭제 완료: {}", key);
        } else {
            log.warn("Redis 키 삭제 실패 또는 키가 존재하지 않습니다: {}", key);
        }
    }

    // 토큰 블랙리스트 처리
    public void blacklistToken(String token) {
        String redisKey = "blacklist:" + token;
        long expiryMillis = getRemainingExpiry(token);  // 남은 유효 기간 계산
        redisTemplate.opsForValue().set(redisKey, "blacklisted", expiryMillis, TimeUnit.MILLISECONDS);
        log.info("토큰 블랙리스트 처리 완료: token 끝자리={}", token.substring(token.length() - 5));
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String token) {
        String redisKey = "blacklist:" + token;
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);

        if (ttl == null || ttl <= 0) {
            log.info("블랙리스트 키가 없거나 만료됐습니다: {}", redisKey);
            return false;
        }

        log.info("블랙리스트 키 존재: {} (TTL: {}ms)", redisKey, ttl);
        return true;
    }

    // 토큰 남은 만료 시간 계산(액세스 토큰 만료 시간)
    private long getRemainingExpiry(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainingTime <= 0) {
            log.warn("토큰 만료 시간이 0 이하입니다: {}", token);
            throw new IllegalArgumentException("토큰이 이미 완료됐습니다.");
        }
        return remainingTime;
    }
}
