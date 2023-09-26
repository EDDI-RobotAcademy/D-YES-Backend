package com.dyes.backend.domain.inquiry.controller;

import com.dyes.backend.domain.inquiry.controller.form.InquiryListResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryReadResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryRegisterRequestForm;
import com.dyes.backend.domain.inquiry.service.InquiryService;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {
    final private InquiryService inquiryService;
    @PostMapping("/register")
    public boolean inquiryRegister(@RequestBody InquiryRegisterRequestForm requestForm) {
        InquiryRegisterRequest request = requestForm.getInquiryRegisterRequest();
        return inquiryService.inquiryRegister(request);
    }
    @GetMapping("/read/{inquiryId}")
    public InquiryReadResponseForm inquiryRead(@PathVariable Long inquiryId) {
        return inquiryService.readInquiry(inquiryId);
    }
    @GetMapping("/list")
    public List<InquiryListResponseForm> inquiryList() {
        return inquiryService.listInquiry();
    }
}
