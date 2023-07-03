package ru.ds.education.currency.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.model.CursRequestModel;
import ru.ds.education.currency.repository.CursRequestRepository;
import ru.ds.education.currency.repository.StatusRepository;
import ru.ds.education.currency.service.CursRequestService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CursRequestServiceImpl implements CursRequestService {

    private final CursRequestRepository cursRequestRepository;

    private final StatusRepository statusRepository;

    @Override
    public Optional<CursRequestModel> getCursRequestByNameAndDate(String name, LocalDate date) {
        return cursRequestRepository.findByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public boolean isExistQueuedCurrency(String name, LocalDate date) {
        return cursRequestRepository.existsByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public void addQueuedCurrency(String name, LocalDate currencyDate, LocalDate requestDate, String correlationId) {
        CursRequestModel cursRequestModel = new CursRequestModel();

        cursRequestModel.setCurrencyName(name);
        cursRequestModel.setCurrencyDate(currencyDate);
        cursRequestModel.setRequestDate(requestDate);
        cursRequestModel.setCorrelationId(correlationId);

        cursRequestRepository.save(cursRequestModel);
    }

    @Override
    public void deleteQueuedCurrency(String name, LocalDate date) {
        cursRequestRepository.deleteByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public boolean checkStatusNotFailed(String name, LocalDate date) {
        return cursRequestRepository.checkStatusNotFailed(name, date).isPresent();
    }

    @Override
    @Transactional
    public void setStatus(String name, LocalDate date, String status) {
        Optional<CursRequestModel> cursRequestModel = cursRequestRepository.findByCurrencyNameAndCurrencyDate(name, date);
        if (cursRequestModel.isPresent()) {
            cursRequestModel.get().setStatusModel(statusRepository.findByStatusName(status));
            cursRequestRepository.save(cursRequestModel.get());
        }
    }
}
