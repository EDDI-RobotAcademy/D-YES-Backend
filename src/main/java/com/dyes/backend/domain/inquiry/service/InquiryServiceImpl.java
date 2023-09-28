package com.dyes.backend.domain.inquiry.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.inquiry.controller.form.InquiryListResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryReadResponseForm;
import com.dyes.backend.domain.inquiry.entity.Inquiry;
import com.dyes.backend.domain.inquiry.entity.InquiryContent;
import com.dyes.backend.domain.inquiry.entity.InquiryType;
import com.dyes.backend.domain.inquiry.entity.Reply;
import com.dyes.backend.domain.inquiry.repository.InquiryContentRepository;
import com.dyes.backend.domain.inquiry.repository.InquiryRepository;
import com.dyes.backend.domain.inquiry.repository.ReplyRepository;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.inquiry.service.request.InquiryReplyRequest;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadInquiryInfoResponse;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadReplyResponse;
import com.dyes.backend.domain.inquiry.service.response.read.InquiryReadUserResponse;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.utility.provider.NaverStmpSecretsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    final private AdminService adminService;
    final private JavaMailSender javaMailSender;
    final private ReplyRepository replyRepository;
    final private NaverStmpSecretsProvider secretsProvider;

    public boolean inquiryRegister(InquiryRegisterRequest request) {
        final String userToken = request.getUserToken();
        final String title = request.getTitle();
        final String content = request.getContent();
        final InquiryType inquiryType = request.getInquiryType();
        final String email = request.getEmail();

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
                    .email(email)
                    .content(inquiryContent)
                    .user(user)
                    .createDate(LocalDate.now())
                    .inquiryType(inquiryType)
                    .inquiryStatus(WAITING)
                    .build();
            inquiryRepository.save(inquiry);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(secretsProvider.getSTMP_EMAIL());
            message.setSubject("문의가 등록 되었습니다");
            message.setText(secretsProvider.getINQUIRY_LINK() + inquiry.getId());
            message.setTo(inquiry.getEmail());

            javaMailSender.send(message);
            log.info("inquiry reply end");

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
                    .userEmail(inquiry.getEmail())
                    .userName(userProfile.getNickName())
                    .build();

            InquiryReadInquiryInfoResponse infoResponse = InquiryReadInquiryInfoResponse.builder()
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent().getContent())
                    .createDate(inquiry.getCreateDate())
                    .inquiryType(inquiry.getInquiryType())
                    .build();

            Optional<Reply> maybeReply = replyRepository.findByInquiry(inquiry);
            InquiryReadReplyResponse replyResponse = new InquiryReadReplyResponse();
            if (maybeReply.isPresent()) {
                Reply reply = maybeReply.get();
                replyResponse = InquiryReadReplyResponse.builder()
                        .replyContent(reply.getReplyContent())
                        .createDate(reply.getCreateDate())
                        .build();
            }

            InquiryReadResponseForm responseForm = new InquiryReadResponseForm(userResponse, infoResponse, replyResponse);
            return responseForm;
        } catch (Exception e) {
            log.error("Error occurred while read inquiry", e);
            return null;
        }
    }
    public List<InquiryListResponseForm> listInquiry() {
        try {
            List<Inquiry> inquiryList = inquiryRepository.findAll();
            return inquiryTransferDto(inquiryList);
        } catch (Exception e) {
            log.error("Error occurred while get inquiry list", e);
            return null;
        }
    }
    public boolean replyInquiry(InquiryReplyRequest request) {
        log.info("inquiry reply start");
        final String userToken = request.getUserToken();
        final Long inquiryId = request.getInquiryId();
        final String content = request.getContent();
        try {
            Admin admin = adminService.findAdminByUserToken(userToken);
            if (admin == null) {
                log.info("admin null");
                return false;
            }

            Optional<Inquiry> maybeInquiry = inquiryRepository.findByIdWithUserContent(inquiryId);
            if (maybeInquiry.isEmpty()){
                log.info("inquiry null");
                return false;
            }
            Inquiry inquiry = maybeInquiry.get();

            Reply reply = Reply.builder()
                    .admin(admin)
                    .inquiry(inquiry)
                    .user(inquiry.getUser())
                    .replyContent(content)
                    .createDate(LocalDate.now())
                    .build();
            replyRepository.save(reply);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(secretsProvider.getSTMP_EMAIL());
            message.setSubject("답변이 등록 되었습니다");
            message.setText(secretsProvider.getINQUIRY_LINK() + inquiry.getId());
            message.setTo(inquiry.getEmail());

            javaMailSender.send(message);
            log.info("inquiry reply end");

            return true;
        } catch (Exception e) {
            log.error("Error occurred while get inquiry reply", e);
            return false;
        }
    }
    public List<InquiryListResponseForm> userInquiryList(String userToken) {
        try {
            User user = authenticationService.findUserByUserToken(userToken);
            if (user == null) {
                log.info("no user, please login again");
                return null;
            }
            List<Inquiry> inquiryList = inquiryRepository.findAllByUser(user);
            return inquiryTransferDto(inquiryList);
        } catch (Exception e) {
            log.error("Error occurred while get inquiry reply", e);
            return null;
        }
    }
    public List<InquiryListResponseForm> inquiryTransferDto(List<Inquiry> inquiryList) {
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
    }
}
