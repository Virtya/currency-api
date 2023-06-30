package ru.ds.education.currency.service;

import java.time.LocalDate;

public interface QueueAddCurService {

    boolean isExistQueuedCurrency(String name, LocalDate date);

    void addQueuedCurrency(String name, LocalDate date);

    void deleteQueuedCurrency(String name, LocalDate date);

}
