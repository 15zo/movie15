package com.example.movie15.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.movie15.domain.movie.service.MovieService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

private final MovieService movieService;

	//fixedDelay = 10000 테스트용 10초마다 한 번 씩
	@Scheduled(cron="0 55 03 * * ?")
	public void updatePopularMoviesJob() throws Exception {
		movieService.savePopularMoviesToDatabase();
		movieService.softDeleteOldMovies();
	}
}
