package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmDeleteForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmOperation;
import com.dyes.backend.domain.farm.repository.FarmOperationRepository;
import com.dyes.backend.domain.farm.repository.FarmRepository;
import com.dyes.backend.domain.farm.service.request.FarmOperationRegisterRequest;
import com.dyes.backend.domain.farm.service.request.FarmRegisterRequest;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoReadResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForm;
import com.dyes.backend.domain.farm.service.response.FarmOperationInfoResponseForm;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.user.entity.Address;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService{
    final private FarmRepository farmRepository;
    final private FarmOperationRepository farmOperationRepository;
    final private ProductRepository productRepository;
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

        Optional<Farm> maybeFarm = farmRepository.findByFarmName(registerRequest.getFarmName());
        if(maybeFarm.isPresent()) {
            log.info("Can not Register Farm: Farm name '{}' already exists", registerRequest.getFarmName());
            return false;
        }

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

    // 농가 삭제
    @Override
    public Boolean deleteFarm(FarmDeleteForm deleteForm) {
        final Admin admin = adminService.findAdminByUserToken(deleteForm.getUserToken());

        if(admin == null) {
            log.info("Can not find Admin");
            return false;
        }

        Optional<Farm> maybeFarm = farmRepository.findById(deleteForm.getFarmId());
        if(maybeFarm.isEmpty()) {
            log.info("Farm is empty");
            return false;
        }

        Farm deleteFarm = maybeFarm.get();

        List<Product> productList = productRepository.findAllByFarm(deleteFarm);
        if(productList.size() > 0) {
            log.info("Can not delete Farm, Product exists");
            return false;
        }

        FarmOperation deleteFarmOperation = farmOperationRepository.findByFarm(deleteFarm);
        farmOperationRepository.delete(deleteFarmOperation);
        farmRepository.delete(deleteFarm);

        return true;
    }

    // 농가 읽기
    @Override
    public FarmInfoReadResponse readFarmInfo(Long farmId) {
        Optional<Farm> maybeFarm  = farmRepository.findById(farmId);
        if(maybeFarm.isEmpty()) {
            log.info("Farm is empty");
            return null;
        }

        Farm farm = maybeFarm.get();
        FarmOperation farmOperation = farmOperationRepository.findByFarm(farm);

        FarmInfoResponseForm farmInfoResponseForm
                = new FarmInfoResponseForm(
                        farm.getId(),
                        farm.getFarmName(),
                        farm.getCsContactNumber(),
                        farm.getFarmAddress(),
                        farm.getMainImage(),
                        farm.getIntroduction(),
                        farm.getProduceTypes());

        FarmOperationInfoResponseForm farmOperationInfoResponseForm
                = new FarmOperationInfoResponseForm(
                        farmOperation.getId(),
                        farmOperation.getBusinessName(),
                        farmOperation.getBusinessNumber(),
                        farmOperation.getRepresentativeName(),
                        farmOperation.getRepresentativeContactNumber());

        FarmInfoReadResponse farmInfoReadResponse = new FarmInfoReadResponse(farmInfoResponseForm, farmOperationInfoResponseForm);

        return farmInfoReadResponse;
    }
}
