package ru.ds.education.currency.service;

import ru.ds.education.currency.dto.CursData;
import ru.ds.education.currency.dto.CurrencyWithResponseCode;

import java.util.List;

public interface CurrencyService {
    List<CursData> getAllCurrencies();

    CursData getCurrency(Long id);

    CurrencyWithResponseCode getCurrencyByNameAndDate(String name, String date);

    CursData addCurrency(CursData newCur);

    CursData updateCurrency(Long id, CursData newCur);

    void deleteCurrency(Long id);

}
