package com.example.movie15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling // 스케줄러 활성화
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) // Page 인터페이스의 직렬화 문제
public class Movie15Application {

	public static void main(String[] args) {
		SpringApplication.run(Movie15Application.class, args);
	}

}
