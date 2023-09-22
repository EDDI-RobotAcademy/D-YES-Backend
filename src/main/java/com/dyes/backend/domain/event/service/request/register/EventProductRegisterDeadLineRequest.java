package com.dyes.backend.domain.event.service.request.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductRegisterDeadLineRequest {
    private LocalDate startLine;
    private LocalDate deadLine;
}
