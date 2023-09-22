package com.dyes.backend.domain.event.controller.form;

import com.dyes.backend.domain.event.service.request.register.EventProductRegisterDeadLineRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterPurchaseCountRequest;
import com.dyes.backend.domain.event.service.request.register.EventProductRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductRegisterRequestForm {
    private EventProductRegisterRequest eventProductRegisterRequest;
    private EventProductRegisterDeadLineRequest eventProductRegisterDeadLineRequest;
    private EventProductRegisterPurchaseCountRequest eventProductRegisterPurchaseCountRequest;
}
