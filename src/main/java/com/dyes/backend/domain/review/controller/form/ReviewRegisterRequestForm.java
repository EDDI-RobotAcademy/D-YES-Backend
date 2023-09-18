package com.dyes.backend.domain.review.controller.form;

import com.dyes.backend.domain.review.service.request.ReviewDetailImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewMainImageRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterRequestForm {
    private String userToken;
    private Long productId;
    private String title;
    private String content;
    private ReviewMainImageRegisterRequest mainImageRegisterRequest;
    private List<ReviewDetailImagesRegisterRequest> detailImagesRegisterRequestList;
}
