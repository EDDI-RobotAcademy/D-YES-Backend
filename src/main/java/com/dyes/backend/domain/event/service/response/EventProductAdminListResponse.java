package com.dyes.backend.domain.event.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProductAdminListResponse {
    private Long eventProductId;
    private String eventProductName;
    private Integer eventPurchaseCount;
    private double discountRate;
    private LocalDate startLine;
    private LocalDate deadLine;
    private int stock;
}
