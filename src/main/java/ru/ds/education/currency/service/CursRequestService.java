package ru.ds.education.currency.service;

import ru.ds.education.currency.model.CursRequestModel;

import java.time.LocalDate;
import java.util.Optional;

public interface CursRequestService {

    Optional<CursRequestModel> getCursRequestByNameAndDate(String name, LocalDate date);

    boolean isExistQueuedCurrency(String name, LocalDate date);

    void addQueuedCurrency(String name, LocalDate currencyDate, LocalDate requestDate, String correlationId);

    void deleteQueuedCurrency(String name, LocalDate date);

    boolean checkStatusNotFailed(String name, LocalDate date);

    void setStatus(String name, LocalDate date, String status);

}
