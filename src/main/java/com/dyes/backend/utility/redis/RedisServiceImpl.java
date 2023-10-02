package com.dyes.backend.utility.redis;

import com.dyes.backend.domain.farmproducePriceForecast.service.request.FarmProducePriceForecastData;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
        log.info("paymentTemporarySaveData start");
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String saveData = objectMapper.writeValueAsString(saveRequest);
        value.set(id, saveData, Duration.ofMinutes(15));
        log.info("paymentTemporarySaveData end");

    }

    public PaymentTemporarySaveRequest getPaymentTemporarySaveData (String id) throws JsonProcessingException {
        log.info("getPaymentTemporarySaveData start");

        ValueOperations<String, String> value = redisTemplate.opsForValue();

        String savedData = value.get(id);

        if (savedData == null) {
            log.info("getPaymentTemporarySaveData end");

            return null;
        }
        PaymentTemporarySaveRequest saveData = objectMapper.readValue(savedData, PaymentTemporarySaveRequest.class);
        log.info("getPaymentTemporarySaveData end");

        return saveData;
    }

    @Override
    public boolean deletePaymentTemporarySaveData(String id) {
        log.info("deletePaymentTemporarySaveData start");

        try {
            redisTemplate.delete(id);
            log.info("deletePaymentTemporarySaveData end");

            return true;
        } catch (Exception e) {
            log.error("Error while deleting payment temporary save data: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void setFarmProducePrice (String farmProduceName, FarmProducePriceForecastData saveRequest) throws JsonProcessingException{
        log.info("farmProducePriceForecastData start");
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String saveData = objectMapper.writeValueAsString(saveRequest);
        value.set(farmProduceName, saveData);
        log.info("farmProducePriceForecastData end");
    }

    @Override
    public FarmProducePriceForecastData getFarmProducePriceForecastData (String farmProduceName) throws JsonProcessingException {
        log.info("getFarmProducePriceForecastData start");

        ValueOperations<String, String> value = redisTemplate.opsForValue();

        String savedData = value.get(farmProduceName);

        if (savedData == null) {
            log.info("getFarmProducePriceForecastData end");

            return null;
        }
        FarmProducePriceForecastData saveData = objectMapper.readValue(savedData, FarmProducePriceForecastData.class);
        log.info("getFarmProducePriceForecastData end");

        return saveData;
    }
}
