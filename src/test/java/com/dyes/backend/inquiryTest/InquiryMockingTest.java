package com.dyes.backend.inquiryTest;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.inquiry.entity.Inquiry;
import com.dyes.backend.domain.inquiry.entity.InquiryContent;
import com.dyes.backend.domain.inquiry.entity.InquiryType;
import com.dyes.backend.domain.inquiry.repository.InquiryContentRepository;
import com.dyes.backend.domain.inquiry.repository.InquiryRepository;
import com.dyes.backend.domain.inquiry.service.InquiryServiceImpl;
import com.dyes.backend.domain.inquiry.service.request.InquiryRegisterRequest;
import com.dyes.backend.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@SpringBootTest
public class InquiryMockingTest {
    @Mock
    private InquiryRepository mockInquiryRepository;
    @Mock
    private AuthenticationService mockAuthenticationService;
    @Mock
    private InquiryContentRepository mockInquiryContentRepository;

    @InjectMocks
    private InquiryServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new InquiryServiceImpl(
                mockInquiryRepository,
                mockInquiryContentRepository,
                mockAuthenticationService
        );
    }
    @Test
    @DisplayName("inquiry mocking test: inquiry register")
    public void 사용자가_문의를_등록합니다() {
        final String userToken = "userToken";
        final String title = "title";
        final String content = "content";

        InquiryRegisterRequest request = new InquiryRegisterRequest(userToken, title, content, InquiryType.PURCHASE);

        User user = new User();
        when(mockAuthenticationService.findUserByUserToken(userToken)).thenReturn(user);

        InquiryContent inquiryContent = new InquiryContent();
        inquiryContent.setContent(content);
        Inquiry inquiry = Inquiry.builder()
                .title(title)
                .user(user)
                .content(inquiryContent)
                .inquiryType(InquiryType.PURCHASE)
                .createDate(LocalDate.now())
                .build();

        mockService.inquiryRegister(request);
        verify(mockInquiryContentRepository, times(1)).save(eq(inquiryContent));
        verify(mockInquiryRepository, times(1)).save(eq(inquiry));
    }
}
