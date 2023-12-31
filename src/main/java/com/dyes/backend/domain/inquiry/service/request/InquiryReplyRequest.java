package com.dyes.backend.domain.inquiry.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReplyRequest {
    private String userToken;
    private Long inquiryId;
    private String content;
}
