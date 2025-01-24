package com.example.movie15.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCleanupScheduler {

    private final RedisTemplate<String, Object> redisTemplate;

    // 주기적으로 Redis의 모든 키를 확인 후 필요 없는 데이터를 정리(매일 새벽 3시)
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredKeys() {
        log.info("Redis 만료되지 않은 키 정리 작업 시작");

        // Redis에 저장된 모든 키 가져오기
        Set<String> keys = redisTemplate.keys("*");
        if (keys == null || keys.isEmpty()) {
            log.info("Redis에 저장된 키가 없습니다.");
            return;
        }

        for (String key : keys) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            if (ttl == null || ttl < 0) {       // TTL이 없는 키는 삭제
                Boolean deleted = redisTemplate.delete(key);
                if (Boolean.TRUE.equals(deleted)) {
                    log.info("만료되지 않은 Redis 키 삭제 완료: {}", key);
                }
            }
        }

        log.info("Redis 만료되지 않은 키 정리 작업 완료");
    }
}
