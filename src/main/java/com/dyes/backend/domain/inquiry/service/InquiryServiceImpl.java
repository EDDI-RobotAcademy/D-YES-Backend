package com.dyes.backend.domain.inquiry.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.inquiry.entity.Inquiry;
import com.dyes.backend.domain.inquiry.entity.InquiryContent;
import com.dyes.backend.domain.inquiry.entity.InquiryType;
import com.dyes.backend.domain.inquiry.repository.InquiryContentRepository;
import com.dyes.backend.domain.inquiry.repository.InquiryRepository;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService{
    final private InquiryRepository inquiryRepository;
    final private InquiryContentRepository inquiryContentRepository;
    final private AuthenticationService authenticationService;

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
                    .build();
            inquiryRepository.save(inquiry);

            return true;
        } catch (Exception e) {
            log.error("Error occurred while register inquiry", e);
            return false;
        }
    }
}
