package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.model.StatusModel;

@Repository
public interface StatusRepository extends JpaRepository<StatusModel, Long> {
    StatusModel findByStatusName(String status);
}
