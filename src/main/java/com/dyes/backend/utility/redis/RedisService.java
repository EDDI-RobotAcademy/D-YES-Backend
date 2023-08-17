package com.dyes.backend.utility.redis;

public interface RedisService {

    void setUserTokenAndUser (String UUID, String userId);
    String getUserId(String userToken);
    void deleteKeyAndValueWithUserToken(String userToken);
}
