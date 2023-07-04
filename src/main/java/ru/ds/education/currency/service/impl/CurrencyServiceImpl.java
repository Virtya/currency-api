package ru.ds.education.currency.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.dto.CursData;
import ru.ds.education.currency.dto.CurrencyWithResponseCode;
import ru.ds.education.currency.dto.CursRequest;
import ru.ds.education.currency.exception.ResourceAlreadyExistException;
import ru.ds.education.currency.exception.ResourceNotFoundException;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.entity.CursDataEntity;
import ru.ds.education.currency.entity.CursRequestEntity;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.service.CurrencyService;
import ru.ds.education.currency.service.CursRequestService;

import java.time.LocalDate;
import java.util.*;

import static ru.ds.education.currency.config.ActiveMQConfig.REQUEST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final MapperCurrency mapper;
    private final ObjectMapper objectMapper;
    private final CursRequestService cursRequestService;
    private final JmsTemplate jmsTemplate;

    @Override
    public List<CursData> getAllCurrencies() {
        List<CursData> cursData = new LinkedList<>();
        List<CursDataEntity> cursDataEntities = currencyRepository.findAll();

        log.info("Отправка запроса на получение всех валют");

        for (CursDataEntity cursDataEntity : cursDataEntities) {
            cursData.add(mapper.map(cursDataEntity, CursData.class));
        }

        return cursData;
    }

    @Override
    public CursData getCurrency(Long id) {
        CursDataEntity cursDataEntity = currencyRepository
                    .findById(id)
                    .orElseThrow(() -> {
                                log.error("Получение: валюты с id = " + id + " не существует");
                                return new ResourceNotFoundException(
                                        "Валюты с id = " + id + " не существует"
                                );
                            }
                    );

        log.info("Получение валюты с id = " + id);

        return mapper.map(cursDataEntity, CursData.class);
    }

    @SneakyThrows
    @Override
    public CurrencyWithResponseCode getCurrencyByNameAndDate(String name, String date) {

        LocalDate actualDate = mapper.map(date, LocalDate.class);

        CursDataEntity cursDataEntity = currencyRepository.findByCurrencyNameAndCursDate(name, actualDate);

        if (cursDataEntity == null) {

            Optional<CursRequestEntity> cursRequestEntityCheck = cursRequestService.getCursRequestByNameAndDate(name, actualDate);

            if (cursRequestEntityCheck.isPresent() &&
                    !Objects.equals(
                            cursRequestEntityCheck
                                    .get()
                                    .getStatusEntity()
                                    .getStatusName(),
                            "FAILED") &&
                    cursRequestService.findByMaxRequestDate(name, actualDate, LocalDate.now()).isPresent()
            ) {
                return new CurrencyWithResponseCode(null, HttpStatus.ACCEPTED);
            }

            String correlationId = (UUID.randomUUID().toString());

            CursRequest cursRequest = cursRequestService.addQueuedCurrency(
                    new CursRequest(
                            name,
                            actualDate,
                            LocalDate.now(),
                            correlationId,
                            null),
                    "CREATED"
            );

            String message = objectMapper.writeValueAsString(cursRequest);

            jmsTemplate.convertAndSend(REQUEST_QUEUE, message);
            cursRequestService.setStatus(correlationId, "SENT");

            log.info("Отправка запроса в адаптер для имени " + name + " на дату " + date);

            return new CurrencyWithResponseCode(null, HttpStatus.NO_CONTENT);
        }

        CursData cursData = mapper.map(cursDataEntity, CursData.class);

        log.info("Получение валюты с именем " + name + ", дата - " + date);
        return new CurrencyWithResponseCode(cursData, HttpStatus.OK);
    }

    @Override
    public CursData addCurrency(CursData newCur) {

        CursDataEntity cursDataEntityCheck = currencyRepository.findByCurrencyNameAndCursDate(
                newCur.getCurrencyName(),
                newCur.getCursDate()
        );

        if (cursDataEntityCheck != null) {
            log.error("Добавление: валюта " + cursDataEntityCheck.getCurrencyName() + " уже существует");
            throw new ResourceAlreadyExistException("Данная валюта уже добавлена");
        }

        CursDataEntity cursDataEntity = mapper.map(newCur, CursDataEntity.class);

        currencyRepository.save(cursDataEntity);

        log.info("Создание валюты с именем " + newCur.getCurrencyName());
        return mapper.map(cursDataEntity, CursData.class);
    }

    @Override
    public CursData updateCurrency(Long id, CursData newCur) {
        CursDataEntity cursData = currencyRepository
                        .findById(id)
                        .orElseThrow(
                                () -> {
                                    log.error("Обновление: валюты с id = " + id + " не существует");
                                    return new ResourceNotFoundException(
                                            "Валюты с id = " + id + " не существует"
                                    );
                                }
                        );

        mapper.map(newCur, cursData);

        currencyRepository.save(cursData);

        log.info("Обновление валюты с именем " + cursData.getCurrencyName());
        return mapper.map(cursData, CursData.class);
    }

    @Override
    public void deleteCurrency(Long id) {
        if (currencyRepository.findById(id).isEmpty()) {
            log.error("Удаление: валюты с id = " + id + " не существует");
            throw new ResourceNotFoundException("Валюты с id = " + id + " не существует");
        }

        log.info("Удаление валюты с id = " + id);
        currencyRepository.deleteById(id);
    }

}
