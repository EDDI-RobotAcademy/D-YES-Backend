package com.dyes.backend.domain.review.service;

import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;

public interface ReviewService {
    boolean beforeMakeReview(ReviewOrderedCheckRequestForm requestForm);
}
