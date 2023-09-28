package com.dyes.backend.inquiryTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.AdminService;
import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.inquiry.controller.form.InquiryListResponseForm;
import com.dyes.backend.domain.inquiry.controller.form.InquiryReadResponseForm;
import com.dyes.backend.domain.inquiry.entity.*;
import com.dyes.backend.domain.inquiry.repository.InquiryContentRepository;
import com.dyes.backend.domain.inquiry.repository.InquiryRepository;
import com.dyes.backend.domain.inquiry.repository.ReplyRepository;
import com.dyes.backend.domain.inquiry.service.InquiryServiceImpl;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.inquiry.service.request.InquiryReplyRequest;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.utility.provider.NaverStmpSecretsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InquiryMockingTest {
    @Mock
    private InquiryRepository mockInquiryRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private InquiryContentRepository mockInquiryContentRepository;
    @Mock
    private UserProfileRepository mockUserProfileRepository;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private JavaMailSender mockJavaMailSender;
    @Mock
    private ReplyRepository mockReplyRepository;
    @Mock
    private NaverStmpSecretsProvider mockNaverStmpSecretsProvider;

    @InjectMocks
    private InquiryServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new InquiryServiceImpl(
                mockInquiryRepository,
                mockInquiryContentRepository,
                mockAuthenticationService,
                mockUserProfileRepository,
                mockAdminService,
                mockJavaMailSender,
                mockReplyRepository,
                mockNaverStmpSecretsProvider
        );
    }
    @Test
    @DisplayName("inquiry mocking test: inquiry register")
    public void 사용자가_문의를_등록합니다() {
        final String userToken = "userToken";
        final String title = "title";
        final String content = "content";
        final String email = "email";

        InquiryRegisterRequest request = new InquiryRegisterRequest(userToken, title, content, email, InquiryType.PURCHASE);

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        InquiryContent inquiryContent = new InquiryContent();
        inquiryContent.setContent(request.getContent());
        Inquiry inquiry = Inquiry.builder()
                .title(request.getTitle())
                .email(request.getEmail())
                .user(user)
                .inquiryStatus(InquiryStatus.WAITING)
                .content(inquiryContent)
                .inquiryType(InquiryType.PURCHASE)
                .createDate(LocalDate.now())
                .build();

        mockService.inquiryRegister(request);
        verify(mockInquiryContentRepository, times(1)).save(eq(inquiryContent));
        verify(mockInquiryRepository, times(1)).save(eq(inquiry));
    }
    @Test
    @DisplayName("inquiry mocking test: inquiry read")
    public void 관리자가_문의를_읽을_수_있습니다() {
        final Long inquiryId = 1L;

        Inquiry inquiry = new Inquiry();
        inquiry.setContent(new InquiryContent());
        when(mockInquiryRepository.findByIdWithUserContent(inquiryId)).thenReturn(Optional.of(inquiry));

        UserProfile userProfile = new UserProfile();
        when(mockUserProfileRepository.findByUser(inquiry.getUser())).thenReturn(Optional.of(userProfile));

        Reply reply = new Reply();
        when(mockReplyRepository.findByInquiry(inquiry)).thenReturn(Optional.of(reply));

        InquiryReadResponseForm result = mockService.readInquiry(inquiryId);
        assertTrue(result != null);
    }
    @Test
    @DisplayName("inquiry mocking test: inquiry list")
    public void 관리자는_문의_목록_페이지를_볼_수_있습니다() {
        Inquiry inquiry = new Inquiry();
        when(mockInquiryRepository.findAll()).thenReturn(List.of(inquiry));

        List<InquiryListResponseForm> result = mockService.listInquiry();
        assertTrue(result != null);
    }
    @Test
    @DisplayName("inquiry mocking test: inquiry reply")
    public void 관리자는_사용자의_문의글에_이메일로_답장을_할_수_있습니다() {
        final Long inquiryId = 1L;
        final String userToken = "userToken";
        final String title = "title";
        final String content = "content";

        InquiryReplyRequest request = new InquiryReplyRequest(userToken, inquiryId, content);

        Admin admin = new Admin();
        when(mockAdminService.findAdminByUserToken(request.getUserToken())).thenReturn(admin);

        Inquiry inquiry = new Inquiry();
        inquiry.setContent(new InquiryContent());
        when(mockInquiryRepository.findByIdWithUserContent(inquiryId)).thenReturn(Optional.of(inquiry));

        boolean result = mockService.replyInquiry(request);
        assertTrue(result);
    }
    @Test
    @DisplayName("inquiry mocking test: user inquiry list")
    public void 사용자는_자신의_문의글_페이지에서_문의글_리스틀_볼_수_있습니다() {
        final String userToken = "userToken";

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        Inquiry inquiry = new Inquiry();
        when(mockInquiryRepository.findAllByUser(user)).thenReturn(List.of(inquiry));

        List<InquiryListResponseForm> result = mockService.userInquiryList(userToken);
        assertTrue(result != null);
    }
}
