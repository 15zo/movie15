package com.example.movie15.domain.inquiry.repository;

import com.example.movie15.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findByUserId(Long userId, Pageable pageable);
}
