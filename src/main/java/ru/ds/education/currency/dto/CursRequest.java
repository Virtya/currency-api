package ru.ds.education.currency.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Формат запроса в базу данных")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CursRequest {

    @Schema(description = "Идентификатор сообщения")
    private String correlationId;

    @Size(min = 3, max = 3, message = "Некорректное сокращение валюты")
    @Schema(description = "Сокращённое название валюты", example = "USD")
    private String currencyName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата получения информации о курсе валюты", example = "2023-06-21")
    private LocalDate currencyDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата отправки запроса о курсе валюты", example = "2023-06-21")
    private LocalDate requestDate;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Статус сообщения")
    private Status status;
}
