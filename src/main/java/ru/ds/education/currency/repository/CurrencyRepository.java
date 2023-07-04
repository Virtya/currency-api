package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.entity.CursDataEntity;

import java.time.LocalDate;

@Repository
public interface CurrencyRepository extends JpaRepository<CursDataEntity, Long> {
    boolean existsByCurrencyName(String name);
    boolean existsByCurrencyNameAndCursDate(String name, LocalDate date);
    boolean existsByCursDate(LocalDate date);
    CursDataEntity findByCurrencyName(String name);
    CursDataEntity findByCurrencyNameAndCursDate(String name, LocalDate date);
    boolean existsByCurrencyCode(Integer currencyCode);
    CursDataEntity findByCurrencyCode(Integer currencyCode);
}
