package com.dyes.backend.domain.review.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestResponse {
    private String productName;
    private List<String> optionNameList;
    private String content;
    private String userNickName;
    private LocalDate createDate;
    private LocalDate purchaseDate;
    private Integer rating;
}
