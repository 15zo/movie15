package com.example.movie15.domain.inquiry.dto;

import com.example.movie15.domain.inquiry.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryStatusRequestDto {
    private InquiryStatus status;
}
