package com.dyes.backend.domain.review.controller;

import com.dyes.backend.domain.review.controller.form.ReviewOrderedCheckRequestForm;
import com.dyes.backend.domain.review.controller.form.ReviewRegisterRequestForm;
import com.dyes.backend.domain.review.service.ReviewService;
import com.dyes.backend.domain.review.service.response.form.ReviewRequestResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/register")
    public boolean registerReviewRequest(@RequestBody ReviewRegisterRequestForm requestForm) {
        return reviewService.registerReview(requestForm);
    }
    @GetMapping("list/{productId}")
    public List<ReviewRequestResponseForm> readReviewRequest(@PathVariable("productId") Long productId) {
        return reviewService.listReview(productId);
    }
}
