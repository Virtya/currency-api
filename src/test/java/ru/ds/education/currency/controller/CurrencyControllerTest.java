package ru.ds.education.currency.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ds.education.currency.ServiceApplicationTest;
import ru.ds.education.currency.exception.ResourceNotFoundException;
import ru.ds.education.currency.entity.CursDataEntity;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.repository.CursRequestRepository;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerTest extends ServiceApplicationTest {

    private final MapperCurrency mapper = new MapperCurrency();
    private final LocalDate currentDate = mapper.map("2023-06-22", LocalDate.class);

    private final String currentDateString = "2023-06-22";
    private final String invalidDateString = "202-06-22";
    private final String presentedCurInDb = "MNT";
    private final String notPresentedCurInDb = "XRP";
    private final String invalidCur = "RUYTRIUE";
    private final Long notPresentedId = 1L;

    @Autowired
    private CurrencyRepository currencyRepository;

    private Long currency1Id;
    private Long currency2Id;
    private Long currency3Id;

    @BeforeAll
    @Transactional
    public void initDb() {
        clearDb();
        CursDataEntity cursDataEntity = new CursDataEntity();
        cursDataEntity.setCurrencyName("MNT");
        cursDataEntity.setCurrencyCode(496);
        cursDataEntity.setCurs(0.02);
        cursDataEntity.setCursDate(currentDate);
        currency1Id = currencyRepository.save(cursDataEntity).getId();

        cursDataEntity = new CursDataEntity();
        cursDataEntity.setCurrencyName("KRW");
        cursDataEntity.setCurrencyCode(410);
        cursDataEntity.setCurs(0.06);
        cursDataEntity.setCursDate(currentDate);
        currency2Id = currencyRepository.save(cursDataEntity).getId();

        cursDataEntity = new CursDataEntity();
        cursDataEntity.setCurrencyName("KZT");
        cursDataEntity.setCurrencyCode(398);
        cursDataEntity.setCurs(0.18);
        cursDataEntity.setCursDate(currentDate);
        currency3Id = currencyRepository.save(cursDataEntity).getId();
    }

    @Test
    @SneakyThrows
    public void getCurrencyTest() {
        String responseJson = readFileFromResource("responses/getCurrencyTestResponse.json");

        mockMvc.perform(
                        get(URI.create("/cur/" + currency1Id))
                                .characterEncoding("utf-8")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson, false));
    }

    @Test
    @SneakyThrows
    public void getAllCurrenciesTest() {
        String responseJson = readFileFromResource("responses/getAllCurrenciesTestResponse.json");

        mockMvc.perform(
                        get(URI.create("/cur"))
                                .characterEncoding("utf-8")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson, false));
    }

    @Test
    @SneakyThrows
    public void createCurrencyTest() {
        String responseJson = readFileFromResource("responses/createCurrencyTestResponse.json");

        mockMvc.perform(
                        post(URI.create("/cur"))
                                .content(readFileFromResource("requests/createCurrencyTestRequest.json"))
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(responseJson, false));
    }

    @Test
    @SneakyThrows
    public void updateCurrencyTest() {
        String responseJson = readFileFromResource("responses/updateCurrencyTestResponse.json");

        mockMvc.perform(
                        put(URI.create("/cur/" + currency2Id))
                                .content(readFileFromResource("requests/updateCurrencyTestRequest.json"))
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().json(responseJson, false));
    }

    @Test
    @SneakyThrows
    public void deleteCurrencyTest() {
        mockMvc.perform(
                        delete(URI.create("/cur/" + currency3Id))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        Optional<CursDataEntity> cursDataModel = currencyRepository.findById(currency3Id);
        assertFalse(cursDataModel.isPresent());
    }

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateTest() {
        String responseJson = readFileFromResource("responses/getCurrencyByNameAndDateTestResponse.json");

        mockMvc.perform(
                        get(URI.create("/cur/" + presentedCurInDb + "/" + currentDateString))
                                .characterEncoding("utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(responseJson, false));
    }

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateFromApiTest() {
        mockMvc.perform(
                        get(URI.create("/cur/" + notPresentedCurInDb + "/" + currentDateString))
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void getCurrencyByIdNotFoundExceptionTest() {
        mockMvc.perform(
                        get(URI.create("/cur/" + notPresentedId))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(
                        result -> assertTrue(
                                result.getResolvedException() instanceof ResourceNotFoundException
                        )
                );
    }

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateConstraintViolationExceptionTest() {
        mockMvc.perform(
                        get(URI.create("/cur/" + invalidCur + "/" + currentDateString))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(
                        result -> assertTrue(
                                result.getResolvedException() instanceof ConstraintViolationException
                        )
                );
    }

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateTimeParseExceptionTest() {
        mockMvc.perform(
                        get(URI.create("/cur/" + presentedCurInDb + "/" + invalidDateString))
                )
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(
                        result -> assertTrue(
                                result.getResolvedException() instanceof DateTimeParseException
                        )
                );
    }

    @AfterAll
    public void clearDb() {
        currencyRepository.deleteAll();
    }
}
