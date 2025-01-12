package com.example.movie15.domain.inquiry.service;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.repository.InquiryRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public InquiryResponseDto createInquiry(InquiryRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Inquiry inquiry = new Inquiry(dto.getSubject(), dto.getContent(), user);
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return mapToResponseDto(savedInquiry);
    }

    // 문의 사항 단일 조회
    public InquiryResponseDto getInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        return mapToResponseDto(inquiry);
    }

    // 문의 사항 페이징 조회
    public Page<InquiryResponseDto> getPagedInquiries(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return inquiryRepository.findByUserId(userId, pageable)
                .map(this::mapToResponseDto);
    }

    public InquiryResponseDto updateInquiry(Long id, InquiryRequestDto dto) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        inquiry.update(dto.getSubject(), dto.getContent());
        Inquiry updatedInquiry = inquiryRepository.save(inquiry);

        return mapToResponseDto(updatedInquiry);
    }

    public void deleteInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다."));

        inquiryRepository.delete(inquiry);
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