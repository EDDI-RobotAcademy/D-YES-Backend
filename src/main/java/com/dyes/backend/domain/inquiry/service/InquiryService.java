package com.dyes.backend.domain.inquiry.service;

import com.dyes.backend.domain.inquiry.controller.form.InquiryListResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryReadResponseForm;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.inquiry.service.request.InquiryReplyRequest;

import java.util.List;

public interface InquiryService {
    boolean inquiryRegister(InquiryRegisterRequest request);
    InquiryReadResponseForm readInquiry(Long inquiryId);
    List<InquiryListResponseForm> listInquiry();
    boolean replyInquiry(InquiryReplyRequest request);
}
