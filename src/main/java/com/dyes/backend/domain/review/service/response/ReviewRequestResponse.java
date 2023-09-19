package com.dyes.backend.domain.review.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestResponse {
    private String productName;
    private String optionName;
    private String content;
    private String userNickName;
    private LocalDate createDate;
    private LocalDate purchaseDate;
    private Integer rating;
}
