package com.dyes.backend.domain.inquiry.controller.form;

import com.dyes.backend.domain.inquiry.entity.InquiryType;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRegisterRequestForm {
    private InquiryRegisterRequest inquiryRegisterRequest;
}