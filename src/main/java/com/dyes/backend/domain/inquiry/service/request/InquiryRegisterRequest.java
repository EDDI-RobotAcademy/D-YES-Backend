package com.dyes.backend.domain.inquiry.service.request;

import com.dyes.backend.domain.inquiry.entity.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRegisterRequest {
    private String userToken;
    private String title;
    private String content;
    private InquiryType inquiryType;
}
