package com.dyes.backend.userTest;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.UserServiceImpl;
import com.dyes.backend.domain.user.service.response.*;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.provider.KakaoOauthSecretsProvider;
import com.dyes.backend.utility.provider.NaverOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private GoogleOauthSecretsProvider mockGoogleOauthSecretsProvider;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private NaverOauthSecretsProvider mockNaverOauthSecretsProvider;
    @Mock
    private KakaoOauthSecretsProvider mockKakaoOauthSecretsProvider;
    @Mock
    private UserProfileRepository mockUserProfileRepository;
    @Mock
    private ObjectMapper mockObjectMapper;
    @InjectMocks
    private UserServiceImpl mockService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockService = new UserServiceImpl(
                mockGoogleOauthSecretsProvider,
                mockNaverOauthSecretsProvider,
                mockKakaoOauthSecretsProvider,
                mockUserRepository,
                mockUserProfileRepository,
                mockRedisService,
                mockRestTemplate,
                mockObjectMapper

        );
    }

    @Test
    @DisplayName("userMockingTest: (google)getAccessTokenWithAuthorizationCode")
    public void 구글_코드로_엑세스토큰을_요청합니다(){

        final String authorizationCode = "구글에서 받은 인가 코드";
        when(mockGoogleOauthSecretsProvider.getGOOGLE_AUTH_CLIENT_ID()).thenReturn("clientId");
        when(mockGoogleOauthSecretsProvider.getGOOGLE_AUTH_REDIRECT_URL()).thenReturn("redirectUrl");
        when(mockGoogleOauthSecretsProvider.getGOOGLE_AUTH_SECRETS()).thenReturn("clientSecret");
        when(mockGoogleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL()).thenReturn("http://example.com/token_request");

        GoogleOauthAccessTokenResponse expectedResponse = new GoogleOauthAccessTokenResponse();

        ResponseEntity<GoogleOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(GoogleOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        GoogleOauthAccessTokenResponse result = mockService.googleRequestAccessTokenWithAuthorizationCode(authorizationCode);
        assertEquals(expectedResponse, result);
    }

    @Test
    @DisplayName("userMockingTest: (google)getUserInfoWithAccessToken")
    public void 구글_엑세스토큰으로_유저정보를_요청합니다() {

        final String accessToken = "구글에서 받은 엑세스 토큰";

        when(mockGoogleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL()).thenReturn("userInfoRequestUrl");

        GoogleOauthUserInfoResponse expectedResponse = new GoogleOauthUserInfoResponse();

        when(mockRestTemplate.exchange(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GoogleOauthUserInfoResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<GoogleOauthUserInfoResponse> actualResponse = mockService.googleRequestUserInfoWithAccessToken(accessToken);

        assertEquals(expectedResponse, actualResponse.getBody());

        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleOauthUserInfoResponse.class));
    }

    @Test
    @DisplayName("userMockingTest: (google)googleUserLogin")
    public void 구글_유저가_로그인을_요청하면_구글에서_코드를_받아_로그인을_하고_UUID를_반환해줍니다() {
        final String authorizationCode = "구글에서 받은 인가 코드";

        GoogleOauthAccessTokenResponse expectedResponse = new GoogleOauthAccessTokenResponse();
        ResponseEntity<GoogleOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(GoogleOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        GoogleOauthUserInfoResponse expectedInfoResponse = new GoogleOauthUserInfoResponse();
        when(mockRestTemplate.exchange(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GoogleOauthUserInfoResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedInfoResponse, HttpStatus.OK));

        User user = new User(expectedInfoResponse.getId(), responseEntity.getBody().getAccessToken(), responseEntity.getBody().getRefreshToken());
        UserProfile userProfile = new UserProfile();

        when(mockService.googleUserSave(responseEntity.getBody(), expectedInfoResponse)).thenReturn(user);
        when(mockUserProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        String result = mockService.googleUserLogin(authorizationCode);
        assertNotNull(result);
    }
    @Test
    @DisplayName("userMockingTest: (google)userSave")
    public void 구글_유저_아이디를_받으면_저장합니다 () {

        GoogleOauthAccessTokenResponse expectedResponse = new GoogleOauthAccessTokenResponse();
        ResponseEntity<GoogleOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(GoogleOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        GoogleOauthUserInfoResponse expectedInfoResponse = new GoogleOauthUserInfoResponse();
        when(mockRestTemplate.exchange(
                eq(mockGoogleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GoogleOauthUserInfoResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedInfoResponse, HttpStatus.OK));


        User user = new User(expectedInfoResponse.getId(), expectedResponse.getAccessToken(), expectedResponse.getRefreshToken());
        when(mockUserRepository.findByStringId(expectedInfoResponse.getId())).thenReturn(Optional.empty());

        User savedUser = mockService.googleUserSave(expectedResponse, expectedInfoResponse);

        when(mockUserRepository.save(user)).thenReturn(user);

        assertEquals(user, savedUser);
    }

    /*
    <------------------------------------------------------------------------------------------------------------------>
     */

    @Test
    @DisplayName("userMockingTest: (naver)getAccessTokenWithAuthorizationCode")
    public void 네이버_코드로_엑세스토큰을_요청합니다(){

        final String authorizationCode = "네이버에서 받은 인가 코드";
        when(mockNaverOauthSecretsProvider.getNAVER_AUTH_CLIENT_ID()).thenReturn("clientId");
        when(mockNaverOauthSecretsProvider.getNAVER_AUTH_REDIRECT_URL()).thenReturn("redirectUrl");
        when(mockNaverOauthSecretsProvider.getNAVER_AUTH_SECRETS()).thenReturn("clientSecret");
        when(mockNaverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL()).thenReturn("http://example.com/token_request");

        NaverOauthAccessTokenResponse expectedResponse = new NaverOauthAccessTokenResponse();

        ResponseEntity<NaverOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockNaverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(NaverOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        NaverOauthAccessTokenResponse result = mockService.naverRequestAccessTokenWithAuthorizationCode(authorizationCode);
        assertEquals(expectedResponse, result);
    }
    @Test
    @DisplayName("userMockingTest: (naver)getUserInfoWithAccessToken")
    public void 네이버_엑세스토큰으로_유저정보를_요청합니다() {

        final String accessToken = "네이버에서 받은 엑세스 토큰";

        when(mockNaverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL()).thenReturn("userInfoRequestUrl");

        NaverOauthUserInfoResponse expectedInfoResponse = new NaverOauthUserInfoResponse();

        JsonNode mockResponseNode = mock(JsonNode.class);
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("response")).thenReturn(mockResponseNode);
        when(mockObjectMapper.convertValue(mockResponseNode, NaverOauthUserInfoResponse.class)).thenReturn(expectedInfoResponse);

        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockJsonNode, HttpStatus.OK);
        when(mockRestTemplate.exchange(
                eq(mockNaverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        NaverOauthUserInfoResponse actualResponse = mockService.naverRequestUserInfoWithAccessToken(accessToken);

        assertEquals(expectedInfoResponse, actualResponse);

        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
    }

    @Test
    @DisplayName("userMockingTest: (naver)googleUserLogin")
    public void 네이버_유저가_로그인을_요청하면_네이버에서_코드를_받아_로그인을_하고_UUID를_반환해줍니다() {
        final String authorizationCode = "구글에서 받은 인가 코드";

        NaverOauthAccessTokenResponse expectedResponse = new NaverOauthAccessTokenResponse();
        ResponseEntity<NaverOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockNaverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(NaverOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        NaverOauthUserInfoResponse expectedInfoResponse = new NaverOauthUserInfoResponse();

        JsonNode mockResponseNode = mock(JsonNode.class);
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("response")).thenReturn(mockResponseNode);
        when(mockObjectMapper.convertValue(mockResponseNode, NaverOauthUserInfoResponse.class)).thenReturn(expectedInfoResponse);

        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockJsonNode, HttpStatus.OK);
        when(mockRestTemplate.exchange(
                eq(mockNaverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        User user = new User(expectedInfoResponse.getId(), responseEntity.getBody().getAccessToken(), responseEntity.getBody().getRefreshToken());
        UserProfile userProfile = new UserProfile();
        when(mockService.naverUserSave(responseEntity.getBody(), expectedInfoResponse)).thenReturn(user);
        when(mockUserProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        String result = mockService.naverUserLogin(authorizationCode);
        assertNotNull(result);
    }

    @Test
    @DisplayName("userMockingTest: (naver)userSave")
    public void 네이버_유저_아이디를_받으면_저장합니다 () {
        NaverOauthAccessTokenResponse expectedResponse = new NaverOauthAccessTokenResponse();
        ResponseEntity<NaverOauthAccessTokenResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(mockRestTemplate.postForEntity(
                eq(mockNaverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL()),
                any(HttpEntity.class),
                eq(NaverOauthAccessTokenResponse.class)
        )).thenReturn(responseEntity);

        NaverOauthUserInfoResponse expectedInfoResponse = new NaverOauthUserInfoResponse();

        JsonNode mockResponseNode = mock(JsonNode.class);
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("response")).thenReturn(mockResponseNode);
        when(mockObjectMapper.convertValue(mockResponseNode, NaverOauthUserInfoResponse.class)).thenReturn(expectedInfoResponse);

        ResponseEntity<JsonNode> mockResponseEntity = new ResponseEntity<>(mockJsonNode, HttpStatus.OK);
        when(mockRestTemplate.exchange(
                eq(mockNaverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL()),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(mockResponseEntity);

        User user = new User(expectedInfoResponse.getId(), expectedResponse.getAccessToken(), expectedResponse.getRefreshToken());
        when(mockUserRepository.findByStringId(expectedInfoResponse.getId())).thenReturn(Optional.empty());

        User savedUser = mockService.naverUserSave(expectedResponse, expectedInfoResponse);

        when(mockUserRepository.save(user)).thenReturn(user);

        assertEquals(user, savedUser);
    }
}
