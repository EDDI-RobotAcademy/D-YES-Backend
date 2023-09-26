package com.dyes.backend.domain.inquiry.controller.form;

import com.dyes.backend.domain.inquiry.entity.InquiryStatus;
import com.dyes.backend.domain.inquiry.entity.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryListResponseForm {
    private Long inquiryId;
    private String title;
    private InquiryType inquiryType;
    private InquiryStatus inquiryStatus;
    private LocalDate createDate;
}
