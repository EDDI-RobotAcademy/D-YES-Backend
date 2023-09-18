package com.dyes.backend.domain.review.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterRequestForm {
    private String userToken;
    private Long productId;
    private String title;
    private String content;
}
