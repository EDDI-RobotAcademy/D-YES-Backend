package com.dyes.backend.domain.inquiry.service.response.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReadReplyResponse {
    private String replyContent;
    private LocalDate createDate;
}
