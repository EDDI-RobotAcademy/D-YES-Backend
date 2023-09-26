package com.dyes.backend.domain.inquiry.service;

import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;

public interface InquiryService {
    boolean inquiryRegister(InquiryRegisterRequest request);
}
