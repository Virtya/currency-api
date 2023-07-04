package ru.ds.education.currency.service;

import ru.ds.education.currency.dto.CursRequest;
import ru.ds.education.currency.entity.CursRequestEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface CursRequestService {

    Optional<CursRequestEntity> getCursRequestByNameAndDate(String name, LocalDate date);

    boolean isExistQueuedCurrency(String name, LocalDate date);

    Optional<CursRequestEntity> findByNameAndDate(String name, LocalDate date);

    CursRequest addQueuedCurrency(CursRequest cursRequest, String status);

    void deleteQueuedCurrency(String name, LocalDate date);

    boolean checkStatusNotFailed(String name, LocalDate date);

    Optional<CursRequestEntity> findByMaxRequestDate(String name, LocalDate date, LocalDate requestDate);

    void setStatus(String correlationId, String status);

}
