package ru.ds.education.currency.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.exception.ResourceAlreadyExistException;
import ru.ds.education.currency.exception.ResourceNotFoundException;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.mapper.MapperDate;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.service.CurrencyService;
import ru.ds.education.currency.service.QueueAddCurService;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static ru.ds.education.currency.config.ActiveMQConfig.REQUEST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final MapperCurrency mapper;
    private final QueueAddCurService queueAddCurService;
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
    public CursDataDto getCurrencyByNameAndDate(String name, String date) {

        LocalDate actualDate = mapperDate.makeDateFromString(date);

        CursDataModel cursDataModel = currencyRepository.findByCurrencyNameAndCursDate(name, actualDate);

        if (cursDataModel == null && !queueAddCurService.isExistQueuedCurrency(name, actualDate)) {

            String message = makeStringJsonMessage(name, date);

            queueAddCurService.addQueuedCurrency(name, actualDate);
            jmsTemplate.convertAndSend(REQUEST_QUEUE, message);

            log.info("Отправка запроса в адаптер для имени " + name + " на дату " + date);
            return null;
        }

        log.info("Получение валюты с именем " + name + ", дата - " + date);
        return mapper.map(cursDataModel, CursDataDto.class);
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

    private String makeStringJsonMessage(String name, String date) {

        JsonObject jsonMessage = Json.createObjectBuilder()
                .add("name", name)
                .add("date", date)
                .build();

        return jsonMessage.toString();
    }

}
