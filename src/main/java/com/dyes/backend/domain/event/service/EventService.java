package com.dyes.backend.domain.event.service;

import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import com.dyes.backend.domain.event.service.response.EventProductListResponse;

import java.util.List;

public interface EventService {
    boolean eventProductRegister(EventProductRegisterRequest productRequest,
                                 EventProductRegisterDeadLineRequest deadLineRequest,
                                 EventProductRegisterPurchaseCountRequest countRequest);
    List<EventProductListResponse> eventProductList();
}
