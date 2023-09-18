package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewMainImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewMainImageRepository extends JpaRepository<ReviewMainImage, Long> {
    ReviewMainImage findByReview(Review review);
}
