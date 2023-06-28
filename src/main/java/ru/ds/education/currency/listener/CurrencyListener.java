package ru.ds.education.currency.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.message.ResponseMessageDto;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.model.CursDataModel;
import ru.ds.education.currency.repository.CurrencyRepository;

import javax.transaction.Transactional;

import static ru.ds.education.currency.config.ActiveMQConfig.RESPONSE_QUEUE;

@Component
public class CurrencyListener {

    private final CurrencyRepository currencyRepository;

    private final MapperCurrency mapper;

    public CurrencyListener(CurrencyRepository currencyRepository, MapperCurrency mapper) {
        this.currencyRepository = currencyRepository;
        this.mapper = mapper;
    }

    @Transactional
    @JmsListener(destination = RESPONSE_QUEUE)
    public void getCurrencyRequest(ResponseMessageDto messageDto) {
        CursDataModel cursDataModel = mapper.map(messageDto, CursDataModel.class);

        currencyRepository.save(cursDataModel);
    }
}
