package com.dyes.backend.utility.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class CommonUtils {
    public static HttpHeaders setHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add("Accept", "application/json");

        return httpHeaders;
    }
}
