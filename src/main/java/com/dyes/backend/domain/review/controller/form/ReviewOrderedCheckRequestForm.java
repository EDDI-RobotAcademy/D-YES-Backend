package com.dyes.backend.domain.review.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOrderedCheckRequestForm {
    private String userToken;
    private Long productId;
}
