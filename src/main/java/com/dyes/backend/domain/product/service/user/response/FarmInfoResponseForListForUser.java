package com.dyes.backend.domain.product.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoResponseForListForUser {
    private String farmName;
    private String mainImage;
    private String representativeName;
}
