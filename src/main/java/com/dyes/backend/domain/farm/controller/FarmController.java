package com.dyes.backend.domain.farm.controller;

import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.FarmService;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 농가 목록 조회
    @GetMapping("/list")
    public List<FarmInfoListResponse> searchFarmList () {
        return farmService.searchFarmList();
    }
}
