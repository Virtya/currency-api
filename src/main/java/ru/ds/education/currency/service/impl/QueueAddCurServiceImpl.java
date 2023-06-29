package ru.ds.education.currency.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.exception.ResourceAlreadyExistException;
import ru.ds.education.currency.exception.ResourceNotFoundException;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.model.QueueAddCurModel;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.repository.QueueAddCurrencyRepository;
import ru.ds.education.currency.service.CurrencyService;
import ru.ds.education.currency.service.QueueAddCurService;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import static ru.ds.education.currency.config.ActiveMQConfig.REQUEST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueAddCurServiceImpl implements QueueAddCurService {

    private final QueueAddCurrencyRepository queueAddCurrencyRepository;

    @Override
    public boolean isExistQueuedCurrency(String name, LocalDate date) {
        return queueAddCurrencyRepository.existsByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public void addQueuedCurrency(String name, LocalDate date) {
        QueueAddCurModel queueAddCurModel = new QueueAddCurModel(name, date);
        queueAddCurrencyRepository.save(queueAddCurModel);
    }

    @Override
    public void deleteQueuedCurrency(String name, LocalDate date) {
        queueAddCurrencyRepository.deleteByCurrencyNameAndCurrencyDate(name, date);
    }
}
