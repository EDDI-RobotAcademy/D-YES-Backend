package com.dyes.backend.domain.farm.controller;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.FarmService;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoListResponseForm;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoReadResponseForm;
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
    public Boolean registerFarm(@RequestBody FarmRegisterRequestForm registerRequestForm) {
        return farmService.registerFarm(registerRequestForm);
    }

    // 농가 목록 조회
    @GetMapping("/list")
    public List<FarmInfoListResponseForm> getFarmList() {
        return farmService.getFarmList();
    }

    // 농가 삭제
    @DeleteMapping("/delete/{farmId}")
    public Boolean deleteFarm(@PathVariable("farmId") Long farmId,
                              @RequestBody FarmDeleteRequestForm deleteRequestForm) {
        return farmService.deleteFarm(farmId, deleteRequestForm);
    }

    // 농가 읽기
    @GetMapping("/read/{farmId}")
    public FarmInfoReadResponseForm readFarm(@PathVariable("farmId") Long farmId) {
        return farmService.readFarm(farmId);
    }

    // 농가 수정
    @PutMapping("/modify/{farmId}")
    public boolean farmModify(@PathVariable("farmId") Long farmId,
                              @RequestBody FarmModifyRequestForm modifyRequestForm) {
        return farmService.modifyFarm(farmId, modifyRequestForm);
    }
}
