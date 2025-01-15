package com.example.movie15.domain.inquiry.service;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.inquiry.repository.InquiryRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public InquiryResponseDto createInquiry(InquiryRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Inquiry inquiry = new Inquiry(dto.getSubject(), dto.getContent(), user);
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return mapToResponseDto(savedInquiry);
    }

    // 문의 사항 조회(사용자 본인)
    public Page<InquiryResponseDto> getUserInquiries(Long userId, Pageable pageable) {
        Page<Inquiry> inquiries = inquiryRepository.findAllByUserId(userId, pageable);
        return inquiries.map(this::mapToResponseDto);
    }

    // 문의 사항 전체 조회(관리자)
    public Page<InquiryResponseDto> getAllInquiries(Pageable pageable) {
        Page<Inquiry> inquiries = inquiryRepository.findAll(pageable);
        return inquiries.map(this::mapToResponseDto);
    }

    // 문의 사항 조회(관리자 -> 특정 사용자)
    public Page<InquiryResponseDto> getInquiriesByUserIdForAdmin(Long userId, Pageable pageable) {
        Page<Inquiry> inquiries = inquiryRepository.findAllByUserId(userId, pageable);
        return inquiries.map(this::mapToResponseDto);
    }

    @Transactional
    public InquiryResponseDto updateInquiry(Long id, InquiryRequestDto dto, Long userId) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        if (inquiry.isAnswered()) {
            throw new IllegalStateException("이미 답변된 문의는 수정할 수 없습니다.");
        }

        if (inquiry.getSubject().equals(dto.getSubject()) && inquiry.getContent().equals(dto.getContent())) {
            throw new IllegalStateException("변경된 내용이 없습니다.");
        }

        inquiry.update(dto.getSubject(), dto.getContent());
        Inquiry updatedInquiry = inquiryRepository.save(inquiry);

        return mapToResponseDto(updatedInquiry);
    }

    // 문의 사항 삭제(사용자)
    @Transactional
    public void deleteInquiry(Long id, String token) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        Long userId = jwtProvider.getUserId(token);

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 문의에 접근할 권한이 없습니다.");
        }

        if (inquiry.isAnswered()) {
            throw new IllegalArgumentException("답변이 완료된 문의는 삭제할 수 없습니다.");
        }

        inquiryRepository.delete(inquiry);
    }

    // 문의 사항 삭제(관리자)
    public void deleteInquiryByAdmin(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));
        inquiryRepository.delete(inquiry);
    }

    @Transactional
    public void updateInquiryStatus(Long id, InquiryStatus status) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        inquiry.changeStatus(status);
        inquiryRepository.save(inquiry);
    }

    private InquiryResponseDto mapToResponseDto(Inquiry inquiry) {
        return new InquiryResponseDto(
                inquiry.getId(),
                inquiry.getSubject(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getCreatedAt().toString(),
                inquiry.getModifiedAt().toString()
        );
    }
}