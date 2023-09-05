package com.dyes.backend.domain.order.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmUserResponse {
    private String email;
    private String contactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;
}
