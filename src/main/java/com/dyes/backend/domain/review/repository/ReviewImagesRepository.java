package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.review.entity.ReviewImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImagesRepository extends JpaRepository<ReviewImages, Long> {
    ReviewImages findByReview(Review review);

    List<ReviewImages> findAllByReview(Review review);
}
