package ru.ds.education.currency;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.ds.education.currency.mapper.MapperCurrency;
import ru.ds.education.currency.mapper.MapperDate;
import ru.ds.education.currency.service.CurrencyService;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    protected MapperCurrency mapper;

    protected MapperDate mapperDate = new MapperDate();

    protected final LocalDate currentDate = mapperDate.makeDateFromString("22-06-2023");

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
