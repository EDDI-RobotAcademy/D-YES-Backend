package com.dyes.backend.utility.redis;

import com.dyes.backend.domain.order.service.user.request.PaymentTemporarySaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    final private RedisTemplate<String, String> redisTemplate;
    final private ObjectMapper objectMapper;
    @Override
    public void setUserTokenAndUser (String userToken, String accessToken) {
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        value.set(userToken, accessToken);
    }

    @Override
    public String getAccessToken(String userToken) {
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String accessToken = value.get(userToken);

        if(accessToken == null) {
            return null;
        }

        return accessToken;
    }

    @Override
    public void deleteKeyAndValueWithUserToken(String userToken){
        try {
            redisTemplate.delete(userToken);
        } catch (RedisException e) {
            log.error("Error while deleting key and value for userToken: {}", userToken, e);
        }
    }

    @Override
    public void paymentTemporarySaveData(String id, PaymentTemporarySaveRequest saveRequest) throws JsonProcessingException {
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String saveData = objectMapper.writeValueAsString(saveRequest);
        value.set(id, saveData);
    }

    public PaymentTemporarySaveRequest getPaymentTemporarySaveData (String id) throws JsonProcessingException {
        ValueOperations<String, String> value = redisTemplate.opsForValue();

        String savedData = value.get(id);

        if (savedData == null) {
            return null;
        }
        PaymentTemporarySaveRequest saveData = objectMapper.readValue(savedData, PaymentTemporarySaveRequest.class);

        return saveData;
    }
}
