package com.dyes.backend.domain.review.controller.form;

import com.dyes.backend.domain.review.service.request.ReviewImagesRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterRequestForm {
    private String userToken;
    private Long orderId;
    private Long productOptionId;
    private String content;
    private Integer rating;
    private List<ReviewImagesRegisterRequest> imagesRegisterRequestList;
}
