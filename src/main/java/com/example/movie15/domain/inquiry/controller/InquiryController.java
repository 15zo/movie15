package com.example.movie15.domain.inquiry.controller;

import java.io.IOException;
import java.util.List;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.inquiry.service.InquiryService;
import com.example.movie15.global.repository.FileRepository;
import com.example.movie15.global.security.JwtProvider;
import com.example.movie15.global.service.FileUploaderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;
    private final JwtProvider jwtProvider;

    // 문의 사항 작성
    @PostMapping
    public ResponseEntity<InquiryResponseDto> createInquiry(
            @RequestPart(name = "inquiry") String inquiryJson,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            @RequestHeader("Authorization") String token) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        InquiryRequestDto dto = objectMapper.readValue(inquiryJson, InquiryRequestDto.class);

        String extractedToken = token.replace("Bearer ", "");
        Long userId = jwtProvider.getUserId(extractedToken);

        InquiryResponseDto response = inquiryService.createInquiry(dto, files, userId);
        return ResponseEntity.status(201).body(response);
    }

    // 문의 사항 조회(사용자 본인)
    @GetMapping("/user")
    public ResponseEntity<Page<InquiryResponseDto>> getUserInquiries(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String extractedToken = token.replace("Bearer ", "");
        Long userId = jwtProvider.getUserId(extractedToken);

        Page<InquiryResponseDto> inquiries = inquiryService.getUserInquiries(userId, pageable);
        return ResponseEntity.ok(inquiries);
    }

    // 문의 사항 전체 조회(관리자)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InquiryResponseDto>> getAllInquiries(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<InquiryResponseDto> response = inquiryService.getAllInquiries(pageable);
        return ResponseEntity.ok(response);
    }

    // 문의 사항 조회(관리자 -> 특정 사용자)
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InquiryResponseDto>> getInquiriesByUserIdForAdmin(
            @PathVariable Long userId,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<InquiryResponseDto> response = inquiryService.getInquiriesByUserIdForAdmin(userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 문의 사항 수정
    @PatchMapping("/{id}")
    public ResponseEntity<InquiryResponseDto> updateInquiry(
        @PathVariable Long id,
        @RequestPart(name = "inquiry") String inquiryJson,
        @RequestPart(name = "files", required = false) List<MultipartFile> files,
        @RequestHeader("Authorization") String token)  throws IOException{

        ObjectMapper objectMapper = new ObjectMapper();
        InquiryRequestDto dto = objectMapper.readValue(inquiryJson, InquiryRequestDto.class);

        String extractedToken = token.replace("Bearer ", "");
        Long userId = jwtProvider.getUserId(extractedToken);

        InquiryResponseDto response = inquiryService.updateInquiry(id, dto, files, userId);
        return ResponseEntity.ok(response);
    }

    // 문의 사항 삭제(사용자) -> 답변 상태: ANSWERED일 경우, 삭제 불가.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInquiry(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        String extractedToken = token.replace("Bearer ", "");
        Long userId = jwtProvider.getUserId(extractedToken);

        inquiryService.deleteInquiry(id, extractedToken);
        return ResponseEntity.ok("삭제되었습니다");
    }

    // 문의 사항 삭제(관리자)
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteInquiryByAdmin(@PathVariable Long id) {

        inquiryService.deleteInquiryByAdmin(id);
        return ResponseEntity.ok("해당 문의는 관리자가 삭제했습니다.");
    }

    // 문의 사항 상태 변경(관리자)
    @PatchMapping("/admin/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateInquiryStatus(
            @PathVariable Long id,
            @RequestParam InquiryStatus status) {

        inquiryService.updateInquiryStatus(id, status);
        return ResponseEntity.ok("문의 사항의 상태가 변경됐습니다.");
    }
}
