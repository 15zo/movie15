package com.example.movie15.domain.inquiry.service;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.inquiry.repository.InquiryRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.*;
import com.example.movie15.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

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
                .orElseThrow(() -> new NotFoundException(ExceptionType.INQUIRY_NOT_FOUND));

        if (inquiry.isAnswered()) {
            throw new ConflictException(ExceptionType.INQUIRY_ALREADY_ANSWERED);
        }

        if (inquiry.getSubject().equals(dto.getSubject()) && inquiry.getContent().equals(dto.getContent())) {
            throw new BadValueException(ExceptionType.INQUIRY_NO_CHANGES);
        }

        inquiry.update(dto.getSubject(), dto.getContent());
        Inquiry updatedInquiry = inquiryRepository.save(inquiry);

        return mapToResponseDto(updatedInquiry);
    }

    // 문의 사항 삭제(사용자)
    @Transactional
    public void deleteInquiry(Long id, Long userId) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.INQUIRY_NOT_FOUND));

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ExceptionType.INQUIRY_FORBIDDEN);
        }

        if (inquiry.isAnswered()) {
            throw new ConflictException(ExceptionType.INQUIRY_ALREADY_ANSWERED);
        }

        inquiryRepository.delete(inquiry);
    }

    // 문의 사항 삭제(관리자)
    public void deleteInquiryByAdmin(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.INQUIRY_NOT_FOUND));
        inquiryRepository.delete(inquiry);
    }

    @Transactional
    public void updateInquiryStatus(Long id, InquiryStatus status) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.INQUIRY_NOT_FOUND));

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