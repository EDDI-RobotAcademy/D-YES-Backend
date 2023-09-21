package com.dyes.backend.domain.farmproducePriceForecast.controller.form;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnalysisRequestForm {
    private int command;
    private String data;

    public AnalysisRequestForm(int command, String data) {
        this.command = command;
        this.data = data;
    }
}
