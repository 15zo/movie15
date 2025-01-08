package com.example.movie15.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie15.global.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {
}
