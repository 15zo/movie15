package com.example.movie15.global.aop.redis;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

	private static final String REDISSON_LOCK_KEY = "LOCK";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.example.movie15.global.aop.redis.DistributedLock)&&args(targetId))")
	public Object lock(final ProceedingJoinPoint joinPoint, Long targetId) throws Throwable {

		Method method = getAnnotation(joinPoint);
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		// Get lock name and acquire lock
		String key = getLockName(targetId, distributedLock); // 키 이름 생성
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

			if (!available) {
				log.error("lock timeout for key: {}", key);
				return false;
			}
			return aopForTransaction.proceed(joinPoint);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
				log.info("Redisson Lock Already Unlock serviceName = {}, Key = {}", method.getName(), REDISSON_LOCK_KEY);
			}
		}
	}

	private Method getAnnotation(ProceedingJoinPoint joinPoint) {
		// Get annotation
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod();
	}

	private String getLockName(Long targetId, DistributedLock annotation) {
		String lockNameFormat = "lock:%s:%s";
		String relevantParameter = targetId.toString();
		return String.format(lockNameFormat, annotation.key(), relevantParameter);
	}
}
