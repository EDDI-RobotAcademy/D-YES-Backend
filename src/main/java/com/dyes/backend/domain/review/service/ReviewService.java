package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.controller.form.ReviewRegisterRequestForm;
import com.dyes.backend.domain.review.service.request.ReviewImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;

import java.util.List;

public interface ReviewService {
    boolean beforeMakeReview(ReviewOrderedCheckRequestForm requestForm);
    boolean registerReview(ReviewRegisterRequest reviewRegisterRequest, List<ReviewImagesRegisterRequest> reviewImagesRegisterRequestList);
    List<ReviewRequestResponseForm> listReview(Long productId);
}
