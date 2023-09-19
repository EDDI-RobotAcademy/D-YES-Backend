package com.dyes.backend.domain.review.service.response.form;

import com.dyes.backend.domain.review.service.response.ReviewRequestImagesResponse;
import com.dyes.backend.domain.review.service.response.ReviewRequestResponse;
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
public class ReviewRequestResponseForm {
    private ReviewRequestResponse reviewRequestResponse;
    private List<ReviewRequestImagesResponse> imagesResponseList;
}
