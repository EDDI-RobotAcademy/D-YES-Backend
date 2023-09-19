package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRatingRepository extends JpaRepository<ReviewRating, Long> {
    Optional<ReviewRating> findByReview(Review review);
}
