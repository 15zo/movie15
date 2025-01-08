package com.example.movie15.domain.inquiry.controller;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의사항 작성
    @PostMapping
    public ResponseEntity<InquiryResponseDto> createInquiry(
            @RequestPart("subject") String subject,
            @RequestPart("content") String content,
            @RequestParam Long userId) {
        InquiryRequestDto dto = new InquiryRequestDto(subject, content);
        InquiryResponseDto response = inquiryService.createInquiry(dto, userId);
        return ResponseEntity.status(201).body(response);
    }


    // 문의사항 페이징 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<InquiryResponseDto>> getPagedInquiries(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InquiryResponseDto> response = inquiryService.getPagedInquiries(userId, page, size);
        return ResponseEntity.ok(response);
    }

    // 문의사항 수정
    @PatchMapping("/{id}")
    public ResponseEntity<InquiryResponseDto> updateInquiry(
            @PathVariable Long id,
            @RequestBody @Valid InquiryRequestDto dto) {
        InquiryResponseDto response = inquiryService.updateInquiry(id, dto);
        return ResponseEntity.ok(response);
    }

    // 문의사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.ok("삭제되었습니다");
    }
}