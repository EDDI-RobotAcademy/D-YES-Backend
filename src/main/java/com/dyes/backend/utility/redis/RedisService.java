package com.dyes.backend.utility.redis;

import com.dyes.backend.domain.farmproducePriceForecast.service.request.FarmProducePriceForecastData;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface RedisService {

    void setUserTokenAndUser (String UUID, String userId);
    String getAccessToken(String userToken);
    void deleteKeyAndValueWithUserToken(String userToken);
    void paymentTemporarySaveData(String id, PaymentTemporarySaveRequest saveRequest) throws JsonProcessingException;
    PaymentTemporarySaveRequest getPaymentTemporarySaveData (String id) throws JsonProcessingException;
    boolean deletePaymentTemporarySaveData (String id);
    void setFarmProducePrice (String farmProduceName, FarmProducePriceForecastData farmProducePriceForecastData) throws JsonProcessingException;
    FarmProducePriceForecastData getFarmProducePriceForecastData (String farmProduceName) throws JsonProcessingException;
}
