package com.dyes.backend.domain.review.service.response.form;

import com.dyes.backend.domain.review.service.response.ReviewRequestDetailImagesResponse;
import com.dyes.backend.domain.review.service.response.ReviewRequestMainImageResponse;
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
    private String title;
    private String content;
    private String userNickName;
    private LocalDate createDate;
    private LocalDate modifyDate;
    private ReviewRequestMainImageResponse mainImageResponse;
    private List<ReviewRequestDetailImagesResponse> detailImagesResponseList;
}
