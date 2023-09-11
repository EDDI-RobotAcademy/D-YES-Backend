package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUserInfoResponse {
    private String userId;
    private String contactNumber;
    private Address address;
}
