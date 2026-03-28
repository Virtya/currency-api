package ru.ds.education.currency.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ds.education.currency.dto.CursData;
import ru.ds.education.currency.dto.CurrencyWithResponseCode;
import ru.ds.education.currency.service.CurrencyService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/cur")
@Tag(name="Валюта", description = "Набор CRUD для работы с валютой")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(
            summary = "Получение валюты",
            description = "Получение валюты по id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CursData> getCurrencyById(
        @PathVariable
        @Min(value = 0, message = "Значение должно быть больше 0")
        Long id
    ){
        return new ResponseEntity<>(currencyService.getCurrency(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение валюты",
            description = "Получение валюты по её названию и дате"
    )
    @GetMapping("/{name}/{date}")
    public ResponseEntity<CursData> getCurrencyByNameAndDate(
        @PathVariable
        @Size(min = 3, max = 3, message = "Некорректное сокращение валюты")
        String name,

        @PathVariable
        String date
    ) {
        CurrencyWithResponseCode currencyWithResponseCode = currencyService.getCurrencyByNameAndDate(name, date);
        return new ResponseEntity<>(currencyWithResponseCode.getCursData(), currencyWithResponseCode.getStatus());
    }

    @Operation(summary = "Получение списка валют")
    @GetMapping
    public ResponseEntity <List<CursData>> getCurrencies(){
        return new ResponseEntity<>(currencyService.getAllCurrencies(), HttpStatus.OK);
    }

    @Operation(summary = "Добавление валюты")
    @PostMapping
    public ResponseEntity<CursData> addCurrency(@RequestBody @Valid CursData currency){
        return new ResponseEntity<>(currencyService.addCurrency(currency), HttpStatus.CREATED);
    }

    @Operation(summary = "Изменение валюты")
    @PutMapping ("/{id}")
    public ResponseEntity<CursData> updateCurrency(
        @PathVariable
        @Min(value = 0, message = "Значение должно быть больше нуля")
        Long id,

        @RequestBody
        @Valid
        CursData currency
    ){
        return new ResponseEntity<>(currencyService.updateCurrency(id, currency), HttpStatus.CREATED);
    }

    @Operation(summary = "Удаление валюты")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteCurrency(@PathVariable @Min(value = 0, message = "Значение должно быть больше 0") Long id){
        currencyService.deleteCurrency(id);
    }
}
