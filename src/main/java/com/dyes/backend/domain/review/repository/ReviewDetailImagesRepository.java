package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewDetailImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDetailImagesRepository extends JpaRepository<ReviewDetailImages, Long> {
    List<ReviewDetailImages> findAllByReview(Review review);
}
