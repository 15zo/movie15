package com.example.movie15.domain.inquiry.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class InquiryRequestDto {
        @NotBlank
        private String subject;

        @NotBlank
        private String content;

        private List<MultipartFile> files;
    }
