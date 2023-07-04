package ru.ds.education.currency.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ds.education.currency.dto.CursRequest;
import ru.ds.education.currency.entity.CursRequestEntity;
import ru.ds.education.currency.mapper.MapperCurrency;
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

    private final MapperCurrency mapper;

    private final CursRequestRepository cursRequestRepository;

    private final StatusRepository statusRepository;

    @Override
    public Optional<CursRequestEntity> getCursRequestByNameAndDate(String name, LocalDate date) {
        return cursRequestRepository.findByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public boolean isExistQueuedCurrency(String name, LocalDate date) {
        return cursRequestRepository.existsByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public Optional<CursRequestEntity> findByNameAndDate(String name, LocalDate date) {
        return cursRequestRepository.findByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public CursRequest addQueuedCurrency(CursRequest cursRequest, String status) {
        CursRequestEntity cursRequestEntity = mapper.map(cursRequest, CursRequestEntity.class);

        cursRequestEntity.setStatusEntity(statusRepository.findByStatusName(status));

        cursRequestRepository.save(cursRequestEntity);
        return mapper.map(cursRequestEntity,CursRequest.class);
    }

    @Override
    @Transactional
    public void deleteQueuedCurrency(String name, LocalDate date) {
        cursRequestRepository.deleteByCurrencyNameAndCurrencyDate(name, date);
    }

    @Override
    public boolean checkStatusNotFailed(String name, LocalDate date) {
        return cursRequestRepository.checkStatusNotFailed(name, date).isPresent();
    }

    @Override
    public Optional<CursRequestEntity> findByMaxRequestDate(String name, LocalDate date, LocalDate requestDate) {
        return cursRequestRepository.findByMaxRequestDate(name, date, requestDate);
    }

    @Override
    @Transactional
    public void setStatus(String correlationId, String status) {
        Optional<CursRequestEntity> cursRequestEntity = cursRequestRepository.findByCorrelationId(correlationId);

        if (cursRequestEntity.isPresent()) {
            cursRequestEntity.get().setStatusEntity(statusRepository.findByStatusName(status));
            cursRequestRepository.save(cursRequestEntity.get());
        }
    }
}
