package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.controller.form.ReviewRegisterRequestForm;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;

public interface ReviewService {
    boolean beforeMakeReview(ReviewOrderedCheckRequestForm requestForm);
    boolean registerReview(ReviewRegisterRequestForm requestForm);
    ReviewRequestResponseForm readReview(Long reviewId);
}
