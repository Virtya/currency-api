package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.model.CursRequestModel;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CursRequestRepository extends JpaRepository<CursRequestModel, Long> {
    boolean existsByCurrencyNameAndCurrencyDate(String name, LocalDate date);
    void deleteByCurrencyNameAndCurrencyDate(String name, LocalDate date);

    Optional<CursRequestModel> findByCurrencyNameAndCurrencyDate(String name,LocalDate date);

    @Query("SELECT c " +
            "FROM CursRequestModel c " +
            "JOIN StatusModel s ON s.id = c.statusModel.id " +
            "WHERE c.currencyName = ?1 AND c.currencyDate = ?2 AND s.statusName != 'FAILED'")
    Optional<CursRequestModel> checkStatusNotFailed(String name, LocalDate date);
}
