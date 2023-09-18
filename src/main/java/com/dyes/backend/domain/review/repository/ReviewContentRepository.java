package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.review.entity.ReviewContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewContentRepository extends JpaRepository<ReviewContent, Long> {
}
