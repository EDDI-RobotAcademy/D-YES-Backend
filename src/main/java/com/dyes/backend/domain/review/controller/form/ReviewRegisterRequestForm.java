package com.dyes.backend.domain.review.controller.form;

import com.dyes.backend.domain.review.service.request.ReviewImagesRegisterRequest;
import com.dyes.backend.domain.review.service.request.ReviewRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterRequestForm {
    private ReviewRegisterRequest reviewRegisterRequest;
    private List<ReviewImagesRegisterRequest> imagesRegisterRequestList;
}
