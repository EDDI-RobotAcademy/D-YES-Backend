package com.dyes.backend.domain.farm.controller;

import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.FarmService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/farm")
public class FarmController {
    final private FarmService farmService;

    // 농가 등록
    @PostMapping("/register")
    public Boolean farmRegister (@RequestBody FarmRegisterRequestForm registerRequestForm) {
        return farmService.farmRegister(registerRequestForm);
    }
}
