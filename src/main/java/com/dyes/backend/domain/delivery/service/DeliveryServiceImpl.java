package com.dyes.backend.domain.delivery.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.delivery.controller.form.DeliveryStatusChangeRequestForm;
import com.dyes.backend.domain.delivery.entity.Delivery;
import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.delivery.repository.DeliveryRepository;
import com.dyes.backend.domain.delivery.service.request.DeliveryStatusChangeRequest;
import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.order.repository.OrderRepository;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.dyes.backend.domain.delivery.entity.DeliveryStatus.DELIVERED;
import static com.dyes.backend.domain.delivery.entity.DeliveryStatus.SHIPPING;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    final private DeliveryRepository deliveryRepository;
    final private OrderRepository orderRepository;
    final private AdminService adminService;

    // 배송 상태 변경
    @Override
    public Boolean changeStatus(DeliveryStatusChangeRequestForm changeRequestForm) {
        log.info("Changing delivery status for ProductOrder with ID: {}", changeRequestForm.getProductOrderId());

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = changeRequestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 배송 상태 변경
        DeliveryStatusChangeRequest deliveryStatusChangeRequest = changeRequestForm.toDeliveryStatusChangeRequest();

        DeliveryStatus deliveryStatus = deliveryStatusChangeRequest.getDeliveryStatus();
        String productOrderId = deliveryStatusChangeRequest.getProductOrderId();
        LocalDate deliveryDate = deliveryStatusChangeRequest.getDeliveryDate();

        try {
            Optional<ProductOrder> maybeProductOrder = orderRepository.findByStringIdWithDelivery(productOrderId);

            if(maybeProductOrder.isEmpty()) {
                log.info("ProductOrder with ID '{}' not found", productOrderId);
                return false;
            }

            ProductOrder productOrder = maybeProductOrder.get();
            Delivery delivery = productOrder.getDelivery();
            delivery.setDeliveryStatus(deliveryStatus);
            if(deliveryStatus.equals(SHIPPING)) {
                delivery.setDepartureDate(deliveryDate);
            } else if(deliveryStatus.equals(DELIVERED)) {
                delivery.setArrivalDate(deliveryDate);
            }
            deliveryRepository.save(delivery);

            productOrder.setDelivery(delivery);
            orderRepository.save(productOrder);

            log.info("Delivery status for ProductOrder with ID {} has been successfully updated.", productOrderId);
            return true;

        } catch (Exception e) {
            log.error("Failed to change the ProductOrder delivery status: {}", e.getMessage(), e);
            return false;
        }
    }
}
