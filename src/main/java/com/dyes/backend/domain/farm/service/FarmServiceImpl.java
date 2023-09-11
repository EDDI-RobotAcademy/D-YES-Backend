package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.farm.controller.form.FarmDeleteRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.farm.repository.*;
import com.dyes.backend.domain.farm.service.request.*;
import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.FarmOperationInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoListResponseForm;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoReadResponseForm;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.repository.ProductRepository;
import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
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
public class FarmServiceImpl implements FarmService {
    final private FarmRepository farmRepository;
    final private FarmBusinessInfoRepository farmBusinessInfoRepository;
    final private FarmCustomerServiceInfoRepository farmCustomerServiceInfoRepository;
    final private FarmIntroductionInfoRepository farmIntroductionInfoRepository;
    final private FarmRepresentativeInfoRepository farmRepresentativeInfoRepository;
    final private ProductRepository productRepository;
    final private AdminService adminService;

    // 농가 등록
    @Override
    public Boolean registerFarm(FarmRegisterRequestForm registerRequestForm) {
        log.info("Registering a new farm");

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = registerRequestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }
        
        // 농가이름 중복 여부 확인
        final FarmRegisterRequest registerRequest = registerRequestForm.toFarmRegisterRequest();

        Optional<Farm> maybeFarm = farmRepository.findByFarmName(registerRequest.getFarmName());
        if (maybeFarm.isPresent()) {
            log.info("Can not Register Farm: Farm name '{}' already exists", registerRequest.getFarmName());
            return false;
        }

        // 농가 등록 진행
        final FarmBusinessInfoRegisterRequest businessInfoRegisterRequest = registerRequestForm.toFarmBusinessInfoRegisterRequest();
        final FarmCustomerServiceInfoRegisterRequest customerServiceInfoRegisterRequest = registerRequestForm.toFarmCustomerServiceInfoRegisterRequest();
        final FarmIntroductionInfoRegisterRequest introductionInfoRegisterRequest = registerRequestForm.toFarmIntroductionInfoRegisterRequest();
        final FarmRepresentativeInfoRegisterRequest representativeInfoRegisterRequest = registerRequestForm.toFarmRepresentativeInfoRegisterRequest();

        try {
            Address address = new Address(
                    customerServiceInfoRegisterRequest.getAddress(),
                    customerServiceInfoRegisterRequest.getZipCode(),
                    customerServiceInfoRegisterRequest.getAddressDetail());

            Farm farm = Farm.builder()
                    .farmName(registerRequest.getFarmName())
                    .build();

            farmRepository.save(farm);

            FarmCustomerServiceInfo farmCustomerServiceInfo = FarmCustomerServiceInfo.builder()
                    .id(farm.getId())
                    .csContactNumber(customerServiceInfoRegisterRequest.getCsContactNumber())
                    .farmAddress(address)
                    .farm(farm)
                    .build();

            farmCustomerServiceInfoRepository.save(farmCustomerServiceInfo);

            FarmIntroductionInfo farmIntroductionInfo = FarmIntroductionInfo.builder()
                    .id(farm.getId())
                    .mainImage(introductionInfoRegisterRequest.getMainImage())
                    .introduction(introductionInfoRegisterRequest.getIntroduction())
                    .produceTypes(introductionInfoRegisterRequest.getProduceTypes())
                    .farm(farm)
                    .build();

            farmIntroductionInfoRepository.save(farmIntroductionInfo);

            FarmBusinessInfo farmBusinessInfo = FarmBusinessInfo.builder()
                    .id(farm.getId())
                    .businessName(businessInfoRegisterRequest.getBusinessName())
                    .businessNumber(businessInfoRegisterRequest.getBusinessNumber())
                    .farm(farm)
                    .build();

            farmBusinessInfoRepository.save(farmBusinessInfo);

            FarmRepresentativeInfo farmRepresentativeInfo = FarmRepresentativeInfo.builder()
                    .id(farm.getId())
                    .representativeName(representativeInfoRegisterRequest.getRepresentativeName())
                    .representativeContactNumber(representativeInfoRegisterRequest.getRepresentativeContactNumber())
                    .farm(farm)
                    .build();

            farmRepresentativeInfoRepository.save(farmRepresentativeInfo);

            log.info("Farm registration successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to register the farm: {}", e.getMessage(), e);
            return false;
        }
    }

    // 농가 목록 조회
    @Override
    public List<FarmInfoListResponseForm> getFarmList() {
        log.info("Reading farm list");

        List<FarmInfoListResponseForm> farmInfoListResponseListForm = new ArrayList<>();

        // 농가 목록 조회 진행
        try {
            List<Farm> farmList = farmRepository.findAll();
            for (Farm farm : farmList) {
                FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(farm);
                FarmInfoListResponseForm farmInfoListResponseForm
                        = new FarmInfoListResponseForm(
                        farm.getId(),
                        farm.getFarmName(),
                        farmCustomerServiceInfo.getFarmAddress());
                farmInfoListResponseListForm.add(farmInfoListResponseForm);
            }

            log.info("Farm list read successful");
            return farmInfoListResponseListForm;

        } catch (Exception e) {
            log.error("Failed to read the farm list: {}", e.getMessage(), e);
            return null;
        }
    }

    // 농가 삭제
    @Override
    public Boolean deleteFarm(Long farmId, FarmDeleteRequestForm deleteRequestForm) {
        log.info("Deleting farm with ID: {}", farmId);

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = deleteRequestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 농가 존재 여부 확인
        Optional<Farm> maybeFarm = farmRepository.findById(farmId);
        if (maybeFarm.isEmpty()) {
            log.info("Farm is empty");
            return false;
        }

        Farm deleteFarm = maybeFarm.get();

        // 농가에 연결된 상품 존재 여부 확인
        List<Product> productList = productRepository.findAllByFarm(deleteFarm);
        if (productList.size() > 0) {
            log.info("Can not delete Farm, Product exists");
            return false;
        }

        // 농가 삭제 진행
        try {
            FarmBusinessInfo deleteFarmBusinessInfo = farmBusinessInfoRepository.findByFarm(deleteFarm);
            FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(deleteFarm);
            FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(deleteFarm);
            FarmRepresentativeInfo farmRepresentativeInfo = farmRepresentativeInfoRepository.findByFarm(deleteFarm);

            farmBusinessInfoRepository.delete(deleteFarmBusinessInfo);
            farmCustomerServiceInfoRepository.delete(farmCustomerServiceInfo);
            farmIntroductionInfoRepository.delete(farmIntroductionInfo);
            farmRepresentativeInfoRepository.delete(farmRepresentativeInfo);
            farmRepository.delete(deleteFarm);

            log.info("Farm deletion successful for farm with ID: {}", farmId);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the farm: {}", e.getMessage(), e);
            return false;
        }
    }

    // 농가 읽기
    @Override
    public FarmInfoReadResponseForm readFarm(Long farmId) {
        log.info("Reading farm with ID: {}", farmId);

        // 농가 존재 여부 확인
        Optional<Farm> maybeFarm = farmRepository.findById(farmId);
        if (maybeFarm.isEmpty()) {
            log.info("Farm is empty");
            return null;
        }

        // 농가 읽기 진행
        try {
            Farm farm = maybeFarm.get();
            FarmBusinessInfo farmBusinessInfo = farmBusinessInfoRepository.findByFarm(farm);
            FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(farm);
            FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);
            FarmRepresentativeInfo farmRepresentativeInfo = farmRepresentativeInfoRepository.findByFarm(farm);

            FarmInfoResponseForAdmin farmInfoResponseForAdmin
                    = new FarmInfoResponseForAdmin().farmInfoResponseForAdmin(farm, farmCustomerServiceInfo, farmIntroductionInfo);

            FarmOperationInfoResponseForAdmin farmOperationInfoResponseForm
                    = new FarmOperationInfoResponseForAdmin().farmOperationInfoResponseForAdmin(farmBusinessInfo, farmRepresentativeInfo);

            FarmInfoReadResponseForm farmInfoReadResponseForm = new FarmInfoReadResponseForm(farmInfoResponseForAdmin, farmOperationInfoResponseForm);

            log.info("Farm read successful for farm with ID: {}", farmId);
            return farmInfoReadResponseForm;

        } catch (Exception e) {
            log.error("Failed to read the farm: {}", e.getMessage(), e);
            return null;
        }
    }

    // 농가 수정
    @Override
    public boolean modifyFarm(Long farmId, FarmModifyRequestForm modifyRequestForm) {
        log.info("Modifying farm with ID: {}", farmId);

        // 관리자 여부 확인
        UserAuthenticationRequest userAuthenticationRequest = modifyRequestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final Admin admin = adminService.findAdminByUserToken(userToken);

        if (admin == null) {
            log.info("Unable to find admin with user token: {}", userToken);
            return false;
        }

        // 농가 존재 여부 확인
        Optional<Farm> maybeFarm = farmRepository.findById(farmId);
        if (maybeFarm.isEmpty()) {
            log.info("Farm is empty");
            return false;
        }

        // 농가 수정 진행
        FarmCustomerServiceInfoModifyRequest farmCustomerServiceInfoModifyRequest
                = modifyRequestForm.toFarmCustomerServiceInfoModifyRequest();
        FarmIntroductionInfoModifyRequest farmIntroductionInfoModifyRequest
                = modifyRequestForm.toFarmIntroductionInfoModifyRequest();
        try {
            Farm farm = maybeFarm.get();

            FarmCustomerServiceInfo farmCustomerServiceInfo = farmCustomerServiceInfoRepository.findByFarm(farm);
            FarmIntroductionInfo farmIntroductionInfo = farmIntroductionInfoRepository.findByFarm(farm);

            farmCustomerServiceInfo.setCsContactNumber(farmCustomerServiceInfoModifyRequest.getCsContactNumber());
            farmIntroductionInfo.setMainImage(farmIntroductionInfoModifyRequest.getMainImage());
            farmIntroductionInfo.setIntroduction(farmIntroductionInfoModifyRequest.getIntroduction());
            farmIntroductionInfo.setProduceTypes(farmIntroductionInfoModifyRequest.getProduceTypes());

            farmCustomerServiceInfoRepository.save(farmCustomerServiceInfo);
            farmIntroductionInfoRepository.save(farmIntroductionInfo);

            log.info("Farm modification successful for farm with ID: {}", farmId);
            return true;

        } catch (Exception e) {
            log.error("Failed to modify the farm: {}", e.getMessage(), e);
            return false;
        }
    }
}
