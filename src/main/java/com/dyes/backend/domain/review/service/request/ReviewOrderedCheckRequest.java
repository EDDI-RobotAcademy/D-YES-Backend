package com.dyes.backend.domain.review.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOrderedCheckRequest {
    private String userToken;
    private Long productId;
}
