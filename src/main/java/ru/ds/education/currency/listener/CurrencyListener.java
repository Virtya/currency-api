package ru.ds.education.currency.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.CursData;
import ru.ds.education.currency.dto.ResponseMessage;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.entity.CursDataEntity;
import ru.ds.education.currency.service.CurrencyService;
import ru.ds.education.currency.service.CursRequestService;

import java.time.LocalDate;
import java.util.Objects;

import static ru.ds.education.currency.config.ActiveMQConfig.RESPONSE_QUEUE;

@Component
@Slf4j
@AllArgsConstructor
public class CurrencyListener {

    private final CurrencyService currencyService;

    private final CursRequestService cursRequestService;

    private final MapperCurrency mapper;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @JmsListener(destination = RESPONSE_QUEUE)
    public void getCurrencyRequest(String message) {

        ResponseMessage responseMessage = objectMapper.readValue(message, ResponseMessage.class);

        String correlationId = responseMessage.getCorrelationId();

        if (Objects.equals(responseMessage.getCurrencyRate(), "error")) {
            cursRequestService.setStatus(correlationId, "FAILED");
        } else {
            cursRequestService.setStatus(correlationId, "PROCESSED");

            CursDataEntity cursDataEntity =
                    new CursDataEntity(
                            null,
                            responseMessage.getCurrencyName(),
                            null,
                            Double.parseDouble(responseMessage.getCurrencyRate()),
                            mapper.map(responseMessage.getCurrencyDate(), LocalDate.class)
                    );

            log.info("Добавление в базу значения валюты с именем " + cursDataEntity.getCurrencyName() +
                    " на дату " + cursDataEntity.getCursDate());

            currencyService.addCurrency(mapper.map(cursDataEntity, CursData.class));

            cursRequestService.setStatus(correlationId, "SUCCEEDED");
        }
    }
}
