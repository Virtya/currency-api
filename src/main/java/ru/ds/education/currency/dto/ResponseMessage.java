package ru.ds.education.currency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
    private String currencyName;

    private String currencyDate;

    private String currencyRate;

    private String correlationId;
}
