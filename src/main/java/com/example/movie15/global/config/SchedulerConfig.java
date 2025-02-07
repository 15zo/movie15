package com.example.movie15.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.movie15.domain.movie.service.MovieService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

private final MovieService movieService;

	//fixedDelay = 10000 테스트용 10초마다 한 번 씩
	//@Scheduled(cron="0 55 03 * * ?")
	@Scheduled(cron="0 50 21 * * ?", zone = "Asia/Seoul")
	public void updatePopularMoviesJob() throws Exception {
		log.info("scheduler start");
		movieService.savePopularMoviesToDatabase();
		movieService.softDeleteOldMovies();
	}
}
