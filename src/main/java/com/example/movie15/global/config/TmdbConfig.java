package com.example.movie15.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix="tmdb")
@Getter
@Setter
public class TmdbConfig {
	private String apiKey;
	private String baseUrl;
}
