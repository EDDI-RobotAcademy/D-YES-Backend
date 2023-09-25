package com.dyes.backend.domain.event.controller.form;

import com.dyes.backend.domain.event.service.response.EventProductAdminListResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductAdminListResponseForm {
    private List<EventProductAdminListResponse> eventProductAdminListResponseList;
}
