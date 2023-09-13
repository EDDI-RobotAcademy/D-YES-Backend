package com.dyes.backend.utility.redis;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{
    final private RedisTemplate<String, Object> redisTemplateObject;
    final private StringRedisTemplate stringRedisTemplate;
    @Override
    public void setUserTokenAndUser (String userToken, String accessToken) {
        ValueOperations<String, String> value = stringRedisTemplate.opsForValue();
        value.set(userToken, accessToken);
    }

    @Override
    public String getAccessToken(String userToken) {
        ValueOperations<String, String> value = stringRedisTemplate.opsForValue();
        String accessToken = value.get(userToken);

        if(accessToken == null) {
            return null;
        }

        return accessToken;
    }

    @Override
    public void deleteKeyAndValueWithUserToken(String userToken){
        try {
            redisTemplateObject.delete(userToken);
        } catch (RedisException e) {
            log.error("Error while deleting key and value for userToken: {}", userToken, e);
        }
    }

    @Override
    public void paymentTemporaryStorage(String id, Object object) {
        ValueOperations<String, Object> value = redisTemplateObject.opsForValue();
        value.set(id, object);
    }

}
