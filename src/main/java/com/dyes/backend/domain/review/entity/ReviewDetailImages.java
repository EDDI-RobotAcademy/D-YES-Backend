package com.dyes.backend.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String detailImgs;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;
}
