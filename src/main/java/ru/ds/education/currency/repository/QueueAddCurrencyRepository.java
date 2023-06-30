package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.model.QueueAddCurModel;

import java.time.LocalDate;

@Repository
public interface QueueAddCurrencyRepository extends JpaRepository<QueueAddCurModel, Long> {
    boolean existsByCurrencyNameAndCurrencyDate(String name, LocalDate date);
    void deleteByCurrencyNameAndCurrencyDate(String name, LocalDate date);
}
