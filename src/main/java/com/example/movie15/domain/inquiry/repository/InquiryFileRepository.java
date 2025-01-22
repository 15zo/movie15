package com.example.movie15.domain.inquiry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.movie15.domain.inquiry.entity.Inquiry;
import com.example.movie15.domain.inquiry.entity.InquiryFile;

public interface InquiryFileRepository extends JpaRepository<InquiryFile, Long> {
	List<InquiryFile> findByInquiry(Inquiry inquiry);
}
