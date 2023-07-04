package ru.ds.education.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ds.education.currency.entity.StatusEntity;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Long> {
    StatusEntity findByStatusName(String status);
}
