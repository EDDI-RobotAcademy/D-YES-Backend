package com.dyes.backend.domain.inquiry.controller.form;

import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadInquiryInfoResponse;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadReplyResponse;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReadResponseForm {
    private InquiryReadUserResponse inquiryReadUserResponse;
    private InquiryReadInquiryInfoResponse inquiryReadInquiryInfoResponse;
    private InquiryReadReplyResponse replyResponse;
}
