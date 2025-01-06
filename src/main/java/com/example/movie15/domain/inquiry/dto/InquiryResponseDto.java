package com.example.movie15.domain.inquiry.dto;

import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class InquiryResponseDto {
        private Long id;
        private String subject;
        private String content;
        private InquiryStatus status;
        private String createdAt;
        private String modifiedAt;
    }