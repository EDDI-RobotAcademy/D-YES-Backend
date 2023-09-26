package com.dyes.backend.domain.inquiry.controller;

import com.dyes.backend.domain.inquiry.controller.form.InquiryRegisterRequestForm;
import com.dyes.backend.domain.inquiry.service.InquiryService;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {
    final private InquiryService inquiryService;

    @PostMapping("/register")
    public boolean inquiryRegister(InquiryRegisterRequestForm requestForm) {
        InquiryRegisterRequest request = requestForm.getInquiryRegisterRequest();
        return inquiryService.inquiryRegister(request);
    }
}
