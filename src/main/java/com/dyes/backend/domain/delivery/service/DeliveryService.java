package com.dyes.backend.domain.delivery.service;

import com.dyes.backend.domain.delivery.controller.form.DeliveryStatusChangeRequestForm;

public interface DeliveryService {
    Boolean changeStatus(DeliveryStatusChangeRequestForm changeRequestForm);
}
