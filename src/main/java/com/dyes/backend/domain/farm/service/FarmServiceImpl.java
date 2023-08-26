package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmOperation;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.service.request.FarmOperationRegisterRequest;
import com.dyes.backend.domain.farm.service.request.FarmRegisterRequest;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.user.entity.Address;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService{
    final private FarmRepository farmRepository;
    final private FarmOperationRepository farmOperationRepository;
    final private AdminService adminService;

    // 농가 등록
    @Override
    public Boolean farmRegister(FarmRegisterRequestForm registerRequestForm) {
        final String userToken = registerRequestForm.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        final FarmRegisterRequest registerRequest = registerRequestForm.toFarmRegisterRequest();
        final FarmOperationRegisterRequest operationRegisterRequest = registerRequestForm.toFarmOperationRegisterRequest();

        Address address = new Address(registerRequest.getAddress(), registerRequest.getZipCode(), registerRequest.getAddressDetail());
        Farm farm = Farm.builder()
                .farmName(registerRequest.getFarmName())
                .csContactNumber(registerRequest.getCsContactNumber())
                .farmAddress(address)
                .mainImage(registerRequest.getMainImage())
                .introduction(registerRequest.getIntroduction())
                .produceTypes(registerRequest.getProduceTypes())
                .build();
        farmRepository.save(farm);

        FarmOperation farmOperation = FarmOperation.builder()
                .id(farm.getId())
                .businessName(operationRegisterRequest.getBusinessName())
                .businessNumber(operationRegisterRequest.getBusinessNumber())
                .representativeName(operationRegisterRequest.getRepresentativeName())
                .representativeContactNumber(operationRegisterRequest.getRepresentativeContactNumber())
                .farm(farm)
                .build();
        farmOperationRepository.save(farmOperation);

        return true;
    }

    // 농가 목록 조회
    @Override
    public List<FarmInfoListResponse> searchFarmList() {

        List<FarmInfoListResponse> farmInfoListResponseList = new ArrayList<>();

        List<Farm> farmList = farmRepository.findAll();
        for(Farm farm: farmList) {
            FarmInfoListResponse farmInfoListResponse = new FarmInfoListResponse(farm.getId(), farm.getFarmName(), farm.getFarmAddress());
            farmInfoListResponseList.add(farmInfoListResponse);
        }
        return farmInfoListResponseList;
    }
}
