package com.dyes.backend.domain.review.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterRequest {
    private String userToken;
    private Long orderId;
    private Long productOptionId;
    private String content;
    private Integer rating;
}
