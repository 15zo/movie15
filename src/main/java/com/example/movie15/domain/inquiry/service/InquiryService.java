package com.example.movie15.domain.inquiry.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.movie15.domain.inquiry.dto.InquiryRequestDto;
import com.example.movie15.domain.inquiry.dto.InquiryResponseDto;
import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.entity.InquiryFile;
import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import com.example.movie15.domain.inquiry.repository.InquiryFileRepository;
import com.example.movie15.domain.inquiry.repository.InquiryRepository;
import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.entity.File;
import com.example.movie15.global.exception.*;
import com.example.movie15.global.model.FileExtension;
import com.example.movie15.global.model.FileType;
import com.example.movie15.global.repository.FileRepository;
import com.example.movie15.global.security.JwtProvider;
import com.example.movie15.global.service.FileUploaderService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final FileUploaderService fileUploaderService;
    private final FileRepository fileRepository;
    private final InquiryFileRepository inquiryFileRepository;

    /**
     *  문의 사항 생성 (첨부파일 포함)
     */
    @Transactional
    public InquiryResponseDto createInquiry(InquiryRequestDto dto, List<MultipartFile> files, Long userId) throws IOException {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ExceptionType.USER_NOT_FOUND));

        Inquiry inquiry = new Inquiry(dto.getSubject(), dto.getContent(), user);
        inquiryRepository.save(inquiry);

        //  파일 업로드 처리 (첨부파일이 있는 경우)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                InquiryFile inquiryFile = uploadFile(file, inquiry);
                inquiry.getInquiryFiles().add(inquiryFile); //  inquiryFiles 리스트에 추가
            }
        }
        inquiryRepository.save(inquiry);
        return new InquiryResponseDto(inquiry);
    }

    /**
     *  파일 업로드 후 InquiryFile 객체 생성
     */
    private InquiryFile uploadFile(MultipartFile file, Inquiry inquiry) {
        try {
            //  파일 업로드
            String fileUrl = fileUploaderService.uploadFile(file);
            if (fileUrl == null || fileUrl.isEmpty()) {
                throw new IOException("파일 업로드 실패");
            }

            //  파일명 검증
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new BadValueException(ExceptionType.INVALID_FILE_EXTENSION);
            }

            //  파일 확장자 검증
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase();
            if (!FileExtension.isValidExtension(extension)) {
                throw new BadValueException(ExceptionType.INVALID_FILE_EXTENSION);
            }

            //  파일 엔티티 생성 후 저장
            File fileEntity = new File(
                fileUrl,
                originalFilename,
                (int) file.getSize(),
                FileExtension.valueOf(extension), //  문자열을 Enum으로 변환
                FileType.INQUIRY
            );

            fileRepository.save(fileEntity);
            return new InquiryFile(inquiry, fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage());
        }
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
    public void deleteInquiry(Long id, String token) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.INQUIRY_NOT_FOUND));

        Long userId = jwtProvider.getUserId(token);

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
            inquiry.getInquiryFiles().stream()
                .map(inquiryFile -> inquiryFile.getFile().getUrl()) // 파일 URL만 추출
                .collect(Collectors.toList()),
                inquiry.getCreatedAt(),
                inquiry.getModifiedAt()
        );
    }
}