package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.model.CursDataModel;

import java.time.LocalDate;

@Repository
public interface CurrencyRepository extends JpaRepository<CursDataModel, Long> {
    boolean existsByCurrencyName(String name);
    boolean existsByCurrencyNameAndAndCursDate(String name, LocalDate date);
    CursDataModel findByCurrencyName(String name);
    CursDataModel findByCurrencyNameAndCursDate(String name, LocalDate date);
    boolean existsByCurrencyCode(Integer currencyCode);
    CursDataModel findByCurrencyCode(Integer currencyCode);
}
