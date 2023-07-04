package ru.ds.education.currency.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "Ошибка")
@AllArgsConstructor
@Data
public class Error {

    @Schema(description = "Описание ошибки", example = "Некорректный формат введённых данных")
    private String message;

    @Schema(description = "Временная метка ошибки", example = "2023-06-21")
    private LocalDate timestamp;
}
