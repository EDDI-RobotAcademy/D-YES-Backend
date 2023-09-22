package com.dyes.backend.domain.event.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductDeadLineResponse {
    private LocalDate startLine;
    private LocalDate deadLine;
}
