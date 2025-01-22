package com.example.movie15.domain.inquiry.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.smartcardio.Card;

import org.springframework.web.multipart.MultipartFile;

import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.entity.InquiryFile;
import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.global.entity.File;

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
        private List<String> fileUrls;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        // ✅ Inquiry 엔티티를 받아 DTO로 변환하는 생성자
        public InquiryResponseDto(Inquiry inquiry) {
            this.id = inquiry.getId();
            this.subject = inquiry.getSubject();
            this.content = inquiry.getContent();
            this.status = inquiry.getStatus();
            this.createdAt = inquiry.getCreatedAt();
            this.modifiedAt = inquiry.getModifiedAt();

            // ✅ 첨부된 파일 URL 리스트 변환
            this.fileUrls = inquiry.getInquiryFiles().stream()
                .map(InquiryFile::getFile) // InquiryFile에서 File 엔티티 추출
                .map(File::getUrl) // File 엔티티에서 URL 추출
                .collect(Collectors.toList());
        }

    }
