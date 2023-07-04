package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.entity.CursRequestEntity;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CursRequestRepository extends JpaRepository<CursRequestEntity, Long> {
    boolean existsByCurrencyNameAndCurrencyDate(String name, LocalDate date);
    void deleteByCurrencyNameAndCurrencyDate(String name, LocalDate date);

    Optional<CursRequestEntity> findByCurrencyNameAndCurrencyDate(String name, LocalDate date);

    Optional<CursRequestEntity> findByCorrelationId(String correlationId);

    @Query("SELECT c " +
            "FROM CursRequestEntity c " +
            "WHERE c.currencyName = ?1 " +
            "AND c.currencyDate = ?2 " +
            "AND ?3 = (" +
            "SELECT MAX(c1.currencyDate) " +
            "FROM CursRequestEntity c1 " +
            "WHERE c1.currencyName = ?1 AND c1.currencyDate = ?2)")
    Optional<CursRequestEntity> findByMaxRequestDate(String name, LocalDate date, LocalDate requestDate);

    @Query("SELECT c " +
            "FROM CursRequestEntity c " +
            "JOIN StatusEntity s ON s.id = c.statusEntity.id " +
            "WHERE c.currencyName = ?1 AND c.currencyDate = ?2 AND s.statusName != 'FAILED'")
    Optional<CursRequestEntity> checkStatusNotFailed(String name, LocalDate date);
}
