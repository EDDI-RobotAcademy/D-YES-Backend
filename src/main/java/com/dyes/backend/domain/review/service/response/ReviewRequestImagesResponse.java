package com.dyes.backend.domain.review.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestImagesResponse {
    private Long reviewImageId;
    private String reviewImages;
}
