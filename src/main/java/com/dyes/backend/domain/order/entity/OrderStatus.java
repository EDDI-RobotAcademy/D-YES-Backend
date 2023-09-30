package com.dyes.backend.domain.order.entity;

public enum OrderStatus {
    READY,
    SEND_TMS,
    OPEN_PAYMENT,
    SELECT_METHOD,
    ARS_WAITING,
    AUTH_PASSWORD,
    ISSUED_SID,
    SUCCESS_PAYMENT,
    PART_CANCEL_PAYMENT,
    CANCEL_PAYMENT,
    EVENT_PAYBACK,
    FAIL_AUTH_PASSWORD,
    QUIT_PAYMENT,
    FAIL_PAYMENT
}
