package com.dyes.backend.domain.event.service.request.delete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductDeleteRequest {
    private String userToken;
    private Long eventProductId;
}
