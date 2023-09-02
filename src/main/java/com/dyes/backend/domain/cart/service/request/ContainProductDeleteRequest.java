package com.dyes.backend.domain.cart.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductDeleteRequest {
    private String userToken;
    private Long productOptionId;
}
