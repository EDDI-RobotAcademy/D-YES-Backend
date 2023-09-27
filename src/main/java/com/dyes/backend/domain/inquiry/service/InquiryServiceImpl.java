package com.dyes.backend.domain.inquiry.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.inquiry.controller.form.InquiryListResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryReadResponseForm;
import com.dyes.backend.domain.inquiry.entity.Inquiry;
import com.dyes.backend.domain.inquiry.entity.InquiryContent;
import com.dyes.backend.domain.inquiry.entity.InquiryStatus;
import com.dyes.backend.domain.inquiry.entity.InquiryType;
import com.dyes.backend.domain.inquiry.repository.InquiryContentRepository;
import com.dyes.backend.domain.inquiry.repository.InquiryRepository;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadInquiryInfoResponse;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadUserResponse;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.inquiry.entity.InquiryStatus.WAITING;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService{
    final private InquiryRepository inquiryRepository;
    final private InquiryContentRepository inquiryContentRepository;
    final private AuthenticationService authenticationService;
    final private UserProfileRepository userProfileRepository;

    public boolean inquiryRegister(InquiryRegisterRequest request) {
        final String userToken = request.getUserToken();
        final String title = request.getTitle();
        final String content = request.getContent();
        final InquiryType inquiryType = request.getInquiryType();

        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                log.info("no user, please login again");
                return false;
            }
            InquiryContent inquiryContent = InquiryContent.builder()
                    .content(content)
                    .build();
            inquiryContentRepository.save(inquiryContent);

            Inquiry inquiry = Inquiry.builder()
                    .title(title)
                    .content(inquiryContent)
                    .user(user)
                    .createDate(LocalDate.now())
                    .inquiryType(inquiryType)
                    .inquiryStatus(WAITING)
                    .build();
            inquiryRepository.save(inquiry);

            return true;
        } catch (Exception e) {
            log.error("Error occurred while register inquiry", e);
            return false;
        }
    }
    public InquiryReadResponseForm readInquiry(Long inquiryId) {
        try {
            Optional<Inquiry> maybeInquiry = inquiryRepository.findByIdWithUserContent(inquiryId);
            if (maybeInquiry.isEmpty()){
                return null;
            }
            Inquiry inquiry = maybeInquiry.get();

            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(inquiry.getUser());
            if (maybeUserProfile.isEmpty()){
                return null;
            }
            UserProfile userProfile = maybeUserProfile.get();

            InquiryReadUserResponse userResponse = InquiryReadUserResponse.builder()
                    .userEmail(userProfile.getEmail())
                    .userName(userProfile.getNickName())
                    .build();

            InquiryReadInquiryInfoResponse infoResponse = InquiryReadInquiryInfoResponse.builder()
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent().getContent())
                    .createDate(inquiry.getCreateDate())
                    .inquiryType(inquiry.getInquiryType())
                    .build();

            InquiryReadResponseForm responseForm = new InquiryReadResponseForm(userResponse, infoResponse);
            return responseForm;
        } catch (Exception e) {
            log.error("Error occurred while read inquiry", e);
            return null;
        }
    }
    public List<InquiryListResponseForm> listInquiry() {
        try {
            List<Inquiry> inquiryList = inquiryRepository.findAll();

            List<InquiryListResponseForm> responseFormList = new ArrayList<>();
            for (Inquiry inquiry : inquiryList) {
                InquiryListResponseForm responseForm = InquiryListResponseForm.builder()
                        .inquiryId(inquiry.getId())
                        .title(inquiry.getTitle())
                        .inquiryType(inquiry.getInquiryType())
                        .inquiryStatus(inquiry.getInquiryStatus())
                        .createDate(inquiry.getCreateDate())
                        .build();
                responseFormList.add(responseForm);
            }
            return responseFormList;
        }catch (Exception e) {
            log.error("Error occurred while get inquiry list", e);
            return null;
        }
    }
}
