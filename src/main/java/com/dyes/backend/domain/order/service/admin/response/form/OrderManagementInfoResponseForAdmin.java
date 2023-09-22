package com.dyes.backend.domain.order.service.admin.response.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderManagementInfoResponseForAdmin {
    private LocalDate orderedTime;
    private int createdOrderCountCount;
}
