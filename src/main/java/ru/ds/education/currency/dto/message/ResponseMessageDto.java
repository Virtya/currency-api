package ru.ds.education.currency.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseMessageDto implements Serializable {
    String currencyName;
    String currencyDate;
    String currencyRate;
}
