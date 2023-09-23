package com.dyes.backend.domain.event.service.request.modify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductModifyDeadLineRequest {
    private LocalDate startLine;
    private LocalDate deadLine;
}
