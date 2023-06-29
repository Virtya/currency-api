package ru.ds.education.currency.service;

import net.bytebuddy.asm.Advice;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.model.QueueAddCurModel;

import java.time.LocalDate;
import java.util.List;

public interface QueueAddCurService {

    boolean isExistQueuedCurrency(String name, LocalDate date);

    void addQueuedCurrency(String name, LocalDate date);

    void deleteQueuedCurrency(String name, LocalDate date);

}
