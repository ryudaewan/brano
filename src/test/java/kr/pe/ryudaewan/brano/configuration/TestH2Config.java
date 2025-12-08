package kr.pe.ryudaewan.brano.configuration;

import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("local")
public class TestH2Config {

    @Bean
    @Primary
    public H2ConsoleProperties h2ConsoleProperties() {
        return new H2ConsoleProperties();
    }
}