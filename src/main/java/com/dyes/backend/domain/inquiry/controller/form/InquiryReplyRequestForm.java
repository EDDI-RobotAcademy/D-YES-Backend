package com.dyes.backend.domain.inquiry.controller.form;

import com.dyes.backend.domain.inquiry.service.request.InquiryReplyRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReplyRequestForm {
    private InquiryReplyRequest inquiryReplyRequest;
}
