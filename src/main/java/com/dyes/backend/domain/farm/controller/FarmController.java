package com.dyes.backend.domain.farm.controller;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.FarmService;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoReadResponse;
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

    // 농가 삭제
    @DeleteMapping("/delete/{farmId}")
    public Boolean deleteFarm (@PathVariable("farmId") Long farmId, @RequestBody FarmDeleteForm deleteForm) {
        return farmService.deleteFarm(farmId, deleteForm);
    }

    // 농가 읽기
    @GetMapping("/read/{farmId}")
    public FarmInfoReadResponse readFarmInfo(@PathVariable("farmId") Long farmId) {
        return farmService.readFarmInfo(farmId);
    }
}
