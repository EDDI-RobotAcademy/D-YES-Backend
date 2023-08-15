package com.dyes.backend.userTest;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.UserServiceImpl;
import com.dyes.backend.domain.user.service.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.user.service.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserMockingTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RedisService mockRedisService;
    @Mock
    private GoogleOauthSecretsProvider mockGoogleOauthClientIdProvider;
    @Mock
    private RestTemplate mockRestTemplate;
    @InjectMocks
    private UserServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new UserServiceImpl(
                mockGoogleOauthClientIdProvider,
                mockUserRepository,
                mockRedisService,
                mockRestTemplate
        );
    }
    @Test
    @DisplayName("userMockingTest: (google)getAccessTokenWithAuthorizationCode")
    public void 구글_코드로_엑세스토큰을_요청합니다(){

        final String authorizationCode = "구글에서 받은 인가 코드";
        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_CLIENT_ID()).thenReturn("clientId");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_REDIRECT_URL()).thenReturn("redirectUrl");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_AUTH_SECRETS()).thenReturn("clientSecret");
        when(mockGoogleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL()).thenReturn("http://example.com/token_request");

        GoogleOauthAccessTokenResponse expectedResponse = new GoogleOauthAccessTokenResponse();

        ResponseEntity<GoogleOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockGoogleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(GoogleOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        GoogleOauthAccessTokenResponse result = mockService.requestAccessTokenWithAuthorizationCode(authorizationCode);
        assertEquals(expectedResponse, result);
    }

    @Test
    @DisplayName("userMockingTest: (google)getUserInfoWithAccessToken")
    public void 구글_엑세스토큰으로_유저정보를_요청합니다() {

        final String accessToken = "구글에서 받은 엑세스 토큰";

        when(mockGoogleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL()).thenReturn("userInfoRequestUrl");

        GoogleOauthUserInfoResponse expectedResponse = new GoogleOauthUserInfoResponse();

        when(mockRestTemplate.exchange(
                eq(mockGoogleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GoogleOauthUserInfoResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<GoogleOauthUserInfoResponse> actualResponse = mockService.requestUserInfoWithAccessToken(accessToken);

        assertEquals(expectedResponse, actualResponse.getBody());

        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleOauthUserInfoResponse.class));

    }

    @Test
    @DisplayName("userMockingTest: (google)userSave")
    public void 구글_유저_아이디를_받으면_저장합니다 () {
        final Long id = 1L;
        final String accessToken = "엑세스토큰";
        final String refreshToken = "리프레시토큰";
        User user = new User(id,accessToken,refreshToken);
        when(mockUserRepository.findById(id)).thenReturn(Optional.empty());

        User savedUser = mockService.userSave(id, accessToken,refreshToken);

        when(mockUserRepository.save(user)).thenReturn(user);

        assertEquals(user, savedUser);
    }

    @Test
    @DisplayName("userMockingTest: (google)googleUserLogin")
    public void 구글_유저가_로그인을_요청하면_구글에서_코드를_받아_로그인을_하고_UUID를_반환해줍니다() {
        final String authorizationCode = "구글에서 받은 인가 코드";

        GoogleOauthAccessTokenResponse expectedResponse = new GoogleOauthAccessTokenResponse();
        ResponseEntity<GoogleOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockGoogleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(GoogleOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        GoogleOauthUserInfoResponse expectedInfoResponse = new GoogleOauthUserInfoResponse();
        when(mockRestTemplate.exchange(
                eq(mockGoogleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GoogleOauthUserInfoResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedInfoResponse, HttpStatus.OK));

        User user = new User(expectedInfoResponse.getId(), responseEntity.getBody().getAccessToken(), responseEntity.getBody().getRefreshToken());
        when(mockService.userSave(expectedInfoResponse.getId(), responseEntity.getBody().getAccessToken(), responseEntity.getBody().getRefreshToken())).thenReturn(user);

        String result = mockService.googleUserLogin(authorizationCode);
        assertNotNull(result);
    }
}
