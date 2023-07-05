package ru.ds.education.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.ds.education.currency.repository.CurrencyRepository;
import ru.ds.education.currency.repository.CursRequestRepository;

import java.nio.file.Files;

@SpringBootTest
@ContextConfiguration(
        classes = ServiceApplication.class,
        initializers = {ServiceApplicationTest.Initializer.class}
)
@ActiveProfiles("test")
public class ServiceApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected CurrencyRepository currencyRepository;

    @Autowired
    protected CursRequestRepository cursRequestRepository;

    @Autowired
    protected JmsTemplate jmsTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @ClassRule
    public static JdbcDatabaseContainer<?> postgreSQLContainer =
            new PostgreSQLContainer("postgres:11.1")
                    .withDatabaseName("currency_ds")
                    .withUsername("postgres")
                    .withPassword("123Nikita2609");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext context) {
            if (!postgreSQLContainer.isRunning()) {
                postgreSQLContainer.start();
            }
            TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl())
                    .applyTo(context.getEnvironment());
        }
    }

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @SneakyThrows
    protected String readFileFromResource(String s) {
        Resource resource = new ClassPathResource(s);
        return Files.readString(resource.getFile().toPath());
    }
}
