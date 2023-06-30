package ru.ds.education.currency.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.model.QueueAddCurModel;
import ru.ds.education.currency.repository.QueueAddCurrencyRepository;
import ru.ds.education.currency.service.QueueAddCurService;

import java.time.LocalDate;

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
        QueueAddCurModel queueAddCurModel = new QueueAddCurModel();

        queueAddCurModel.setCurrencyName(name);
        queueAddCurModel.setCurrencyDate(date);
        queueAddCurrencyRepository.save(queueAddCurModel);
    }

    @Override
    public void deleteQueuedCurrency(String name, LocalDate date) {
        queueAddCurrencyRepository.deleteByCurrencyNameAndCurrencyDate(name, date);
    }
}
