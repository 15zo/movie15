package com.example.movie15.domain.inquiry.dto;

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
    }