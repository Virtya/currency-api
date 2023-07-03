package ru.ds.education.currency.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.dto.CursRequestDto;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.mapper.MapperDate;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.model.CursRequestModel;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.service.CursRequestService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static ru.ds.education.currency.config.ActiveMQConfig.RESPONSE_QUEUE;

@Component
@Slf4j
@AllArgsConstructor
public class CurrencyListener {

    private final CurrencyRepository currencyRepository;

    private final CursRequestService cursRequestService;

    private final MapperCurrency mapperCurrency;

    private final MapperDate mapperDate;

    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    @JmsListener(destination = RESPONSE_QUEUE)
    public void getCurrencyRequest(String message) {

        JsonNode jsonNode = objectMapper.readTree(message);

        String name = jsonNode.get("currencyName").asText();
        String dateString = jsonNode.get("currencyDate").asText();

        LocalDate date = mapperDate.makeDateFromString(dateString);

        if (Objects.equals(jsonNode.get("correlationId").asText(), "error")) {
            cursRequestService.setStatus(name, date, "FAILED");
        } else {
            cursRequestService.setStatus(name, date, "PROCESSED");

            CursDataDto cursDataDto =
                    new CursDataDto(
                            name,
                            null,
                            jsonNode.get("currencyRate").asDouble(),
                            date
                    );

            CursDataModel cursDataModel = mapperCurrency.map(cursDataDto, CursDataModel.class);

            log.info("Добавление в базу значения валюты с именем " + cursDataModel.getCurrencyName() +
                    " на дату " + cursDataModel.getCursDate());
            currencyRepository.save(cursDataModel);
            cursRequestService.deleteQueuedCurrency(name, date);
        }
    }
}
