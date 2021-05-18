package com.marvel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.marvel.record.CharactersRecord;
import com.marvel.record.ComicsRecord;
import org.junit.ClassRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Comparator;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractIntegrationTest {

    protected static final Faker FAKER = Faker.instance();
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final Comparator<CharactersRecord> CHARACTER_RECORD_COMPARATOR = Comparator.comparingLong(CharactersRecord::getId);
    protected static final Comparator<ComicsRecord> COMIC_RECORD_COMPARATOR = Comparator.comparingLong(ComicsRecord::getId);

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("marver") // typo in application.properties
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgreSQLContainer.start();
        System.setProperty("POSTGRES_HOST", postgreSQLContainer.getContainerIpAddress());
        System.setProperty("POSTGRES_PORT", String.valueOf(postgreSQLContainer.getMappedPort(5432)));
    }

    protected MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

}
