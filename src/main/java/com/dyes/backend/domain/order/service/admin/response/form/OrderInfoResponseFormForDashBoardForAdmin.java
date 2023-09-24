package com.dyes.backend.domain.order.service.admin.response.form;

import com.dyes.backend.domain.order.service.admin.response.OrderInfoResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoResponseFormForDashBoardForAdmin{
    private List<OrderInfoResponseForAdmin> orderInfoResponseForAdminList;
    private List<OrderManagementInfoResponseForAdmin> createdOrderCountList;
}