package com.dyes.backend.domain.event.controller.form;

import com.dyes.backend.domain.event.service.request.delete.EventProductDeleteRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductDeleteRequestForm {
    private EventProductDeleteRequest eventProductDeleteRequest;
}
