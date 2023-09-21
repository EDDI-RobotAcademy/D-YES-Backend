package com.dyes.backend.domain.review.repository;

import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.review.entity.Review;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProduct(Product product);
    @Query("SELECT r FROM Review r JOIN FETCH r.product p JOIN FETCH r.productOrder o WHERE r.user = :user")
    List<Review> findAllByUserWithProductAndOrder(@Param("user") User user);
}
