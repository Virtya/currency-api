package ru.ds.education.currency.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.jms.annotation.JmsListener;
import ru.ds.education.currency.ServiceApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerIntegrationTest extends ServiceApplicationTest {
    private String receivedMessage;

    @Test
    @SneakyThrows
    public void getCurrencyByNameAndDateFromApiTest() {
        String requestMessage = readFileFromResource("requests/sendCurrencyInQueue.json");

        jmsTemplate.convertAndSend("test-queue", requestMessage);

        waitForMessage();

        assertEquals(receivedMessage, requestMessage);
    }

    @JmsListener(destination = "test-queue")
    public void receiveMessage(String message) {
        receivedMessage = message;
    }

    @SneakyThrows
    public void waitForMessage() {
        Thread.sleep(1000);
    }
}
