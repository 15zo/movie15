package com.example.movie15.domain.runtime.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.movie15.domain.runtime.dto.RunTimeRequestDto;
import com.example.movie15.domain.runtime.dto.RunTimeResponseDto;
import com.example.movie15.domain.runtime.service.RunTimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/runtimes")
public class RuntimeController {
	private final RunTimeService runTimeService;

	@PostMapping
	public ResponseEntity<RunTimeResponseDto> createRunTime(@RequestBody RunTimeRequestDto requestDto) {
		RunTimeResponseDto createdRunTime = runTimeService.createRunTime(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRunTime);
	}
}
