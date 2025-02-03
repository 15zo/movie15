package com.example.movie15.global.config;


import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketTimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SchedulerConfigTest {

	@Test
	@DisplayName("RestTemplate 타임아웃 테스트")
	public void testRestTemplateTimeout() {
		// RestTemplate에 Timeout 설정
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10000); // 5초 연결 타임아웃
		factory.setReadTimeout(11000);    // 5초 읽기 타임아웃

		RestTemplate restTemplate = new RestTemplate(factory);

		// given (실제로 존재하지 않는 URL 또는 응답이 느린 API)
		String url = "http://www.google.com:81"; // 의도적으로 응답이 없는 IP

		// when, then (예외 유형을 구분하여 검증)
		try {
			restTemplate.exchange(url, HttpMethod.GET, null, String.class);
			fail("예외가 발생해야 합니다."); // 예외가 발생하지 않으면 실패
		} catch (ResourceAccessException e) {
			Throwable cause = e.getCause();

			if (cause instanceof ConnectTimeoutException) {
				log.info(" 연결 타임아웃 (ConnectTimeoutException) 발생");
			} else if (cause instanceof SocketTimeoutException) {
				log.info(" 읽기 타임아웃 (SocketTimeoutException) 발생");
			} else {
				log.info("️ 예상하지 못한 예외 발생: " + cause.getClass().getName());
			}
			assertTrue(cause instanceof ConnectTimeoutException || cause instanceof SocketTimeoutException);
		}
	}
}