package ru.ds.education.currency.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.dto.CurrencyWithResponseCodeDto;
import ru.ds.education.currency.exception.ResourceAlreadyExistException;
import ru.ds.education.currency.exception.ResourceNotFoundException;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.mapper.MapperDate;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.model.CursRequestModel;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.service.CurrencyService;
import ru.ds.education.currency.service.CursRequestService;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static ru.ds.education.currency.config.ActiveMQConfig.REQUEST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final MapperCurrency mapper;
    private final CursRequestService cursRequestService;
    private final JmsTemplate jmsTemplate;
    private final MapperDate mapperDate;

    @Override
    public List<CursDataDto> getAllCurrencies() {
        List<CursDataDto> cursDataDtos = new LinkedList<>();
        List<CursDataModel> cursDataModels = currencyRepository.findAll();

        log.info("Отправка запроса на получение всех валют");

        for (CursDataModel cursDataModel : cursDataModels) {
            cursDataDtos.add(mapper.map(cursDataModel, CursDataDto.class));
        }

        return cursDataDtos;
    }

    @Override
    public CursDataDto getCurrency(Long id) {
        CursDataModel cursDataModel = currencyRepository
                    .findById(id)
                    .orElseThrow(() -> {
                                log.error("Получение: валюты с id = " + id + " не существует");
                                return new ResourceNotFoundException(
                                        "Валюты с id = " + id + " не существует"
                                );
                            }
                    );

        log.info("Получение валюты с id = " + id);

        return mapper.map(cursDataModel, CursDataDto.class);
    }

    @SneakyThrows
    @Override
    public CurrencyWithResponseCodeDto getCurrencyByNameAndDate(String name, String date) {

        LocalDate actualDate = mapperDate.makeDateFromString(date);

        CursDataModel cursDataModel = currencyRepository.findByCurrencyNameAndCursDate(name, actualDate);

        if (cursDataModel == null) {

            if (cursRequestService.isExistQueuedCurrency(name, actualDate) &&
                    cursRequestService.checkStatusNotFailed(name, actualDate)) {
                return new CurrencyWithResponseCodeDto(null, HttpStatus.ACCEPTED);
            }

            String correlationId = (UUID.randomUUID().toString());

            cursRequestService.addQueuedCurrency(name, actualDate, LocalDate.now(), correlationId);
            cursRequestService.setStatus(name, actualDate, "CREATED");

            CursRequestModel cursRequestModel = cursRequestService.getCursRequestByNameAndDate(name, actualDate).get();

            String message = makeStringJsonMessage(
                    name,
                    date,
                    cursRequestModel.getRequestDate().toString(),
                    correlationId,
                    cursRequestModel.getStatusModel().getStatusName()
            );

            jmsTemplate.convertAndSend(REQUEST_QUEUE, message);
            cursRequestService.setStatus(name, actualDate, "SENT");

            log.info("Отправка запроса в адаптер для имени " + name + " на дату " + date);

            return new CurrencyWithResponseCodeDto(null, HttpStatus.NO_CONTENT);
        }

        CursDataDto cursDataDto = mapper.map(cursDataModel, CursDataDto.class);

        log.info("Получение валюты с именем " + name + ", дата - " + date);
        return new CurrencyWithResponseCodeDto(cursDataDto, HttpStatus.OK);
    }

    @Override
    public CursDataDto addCurrency(CursDataDto newCur) {

        if (currencyRepository.existsByCurrencyName(newCur.getCurrencyName())
                || currencyRepository.existsByCurrencyCode(newCur.getCurrencyCode())) {
            log.error("Добавление: валюта " + newCur.getCurrencyName() + " уже существует");
            throw new ResourceAlreadyExistException("Данная валюта уже добавлена");
        }

        CursDataModel cursDataModel = new CursDataModel();

        cursDataModel.setCurrencyName(newCur.getCurrencyName());
        cursDataModel.setCurrencyCode(newCur.getCurrencyCode());
        cursDataModel.setCurs(newCur.getCurs());
        cursDataModel.setCursDate(newCur.getCursDate());

        currencyRepository.save(cursDataModel);

        log.info("Создание валюты с именем " + newCur.getCurrencyName());
        return mapper.map(cursDataModel, CursDataDto.class);
    }

    @Override
    public CursDataDto updateCurrency(Long id, CursDataDto newCur) {
        CursDataModel cursData = currencyRepository
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
        return mapper.map(cursData, CursDataDto.class);
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

    private String makeStringJsonMessage(String name, String currencyDate, String requestDate,
                                         String correlationId, String status) {

        JsonObject jsonMessage = Json.createObjectBuilder()
                .add("currencyName", name)
                .add("currencyDate", currencyDate)
                .add("requestDate", requestDate)
                .add("correlationId", correlationId)
                .add("status", status)
                .build();

        return jsonMessage.toString();
    }

}
