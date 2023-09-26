package com.dyes.backend.domain.order.service.admin.response.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyOrdersStatisticsResponseForm {
    private int totalOrdersCount;
    private int completedOrders;
    private int cancelledOrders;
    private int totalOrdersAmount;
    private double monthOverMonthGrowthRate;
    private List<Integer> orderCountListByDay = new ArrayList<>();
}
