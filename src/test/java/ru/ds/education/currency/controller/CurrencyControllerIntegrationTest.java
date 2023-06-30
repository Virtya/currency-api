package ru.ds.education.currency.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import ru.ds.education.currency.ServiceApplicationTest;
import ru.ds.education.currency.repository.CurrencyRepository;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ds.education.currency.config.ActiveMQConfig.REQUEST_QUEUE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerIntegrationTest extends ServiceApplicationTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    private JmsTemplate jmsTemplate;

    private EmbeddedActiveMQBroker embeddedActiveMQBroker;

    @BeforeEach
    public void setUp() {
        embeddedActiveMQBroker.start();
    }

    @AfterEach
    public void tearDown() {
        embeddedActiveMQBroker.stop();
    }

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateFromApiTest() {
        String name = "XRP";
        String date = "22-06-2023";

        JsonObject jsonMessage = Json.createObjectBuilder()
                .add("name", name)
                .add("date", date)
                .build();

        String message = jsonMessage.toString();

        jmsTemplate.convertAndSend(REQUEST_QUEUE, message);

        Thread.sleep(1500);

        assertTrue(currencyRepository.existsByCurrencyNameAndAndCursDate(name, currentDate));
    }
}
