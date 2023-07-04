package ru.ds.education.currency.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Schema(description = "Статус сообщения")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Status {

    @Schema(description = "Информация о статусе", example = "PROCESSED")
    private String statusName;

}
