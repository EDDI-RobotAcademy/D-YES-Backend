package com.dyes.backend.domain.review.controller;

import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    final private ReviewService reviewService;
    @PostMapping("/check")
    public boolean beforeRegisterReviewCheck (@RequestBody ReviewOrderedCheckRequestForm requestForm) {
        return reviewService.beforeMakeReview(requestForm);
    }
}
