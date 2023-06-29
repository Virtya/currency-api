package ru.ds.education.currency.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.service.QueueAddCurService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ru.ds.education.currency.config.ActiveMQConfig.RESPONSE_QUEUE;

@Component
@Slf4j
@AllArgsConstructor
public class CurrencyListener {

    private final CurrencyRepository currencyRepository;

    private final QueueAddCurService queueAddCurService;

    private final MapperCurrency mapper;

    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    @JmsListener(destination = RESPONSE_QUEUE)
    public void getCurrencyRequest(String message) {

        JsonNode jsonNode = objectMapper.readTree(message);

        String name = jsonNode.get("currencyName").asText();
        String date = jsonNode.get("currencyDate").asText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate actualDate = LocalDate.parse(date, formatter);

        CursDataDto cursDataDto =
                new CursDataDto(
                        name,
                        null,
                        jsonNode.get("currencyRate").asDouble(),
                        actualDate
        );

        CursDataModel cursDataModel = mapper.map(cursDataDto, CursDataModel.class);

        log.info("Добавление в базу значения валюты с именем " + cursDataModel.getCurrencyName() +
                " на дату " + cursDataModel.getCursDate());
        currencyRepository.save(cursDataModel);
        queueAddCurService.deleteQueuedCurrency(name, actualDate);
    }
}
