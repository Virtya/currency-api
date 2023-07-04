package ru.ds.education.currency.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Schema(description = "Объект для возврата валюты и кода ответа")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CurrencyWithResponseCode {

    @Schema(description = "Информация о валюте")
    private CursData cursData;

    @Schema(description = "Статус ответа", example = "OK")
    private HttpStatus status;

}
