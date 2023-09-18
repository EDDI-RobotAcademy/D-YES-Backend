package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r JOIN FETCH r.ReviewContent WHERE r.id = :reviewId")
    Optional<Review> findByIdWithContent(@Param("reviewId") Long reviewId);
}
