package com.dyes.backend.domain.admin.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequest {
    private String id;
    private String name;
}
