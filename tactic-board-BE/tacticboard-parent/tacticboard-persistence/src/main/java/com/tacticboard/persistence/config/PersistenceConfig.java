package com.tacticboard.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tacticboard.persistence.repository")
@EntityScan(basePackages = "com.tacticboard.core.model.entity")
@EnableJpaAuditing
public class PersistenceConfig {

    @Bean
    public TimeZone timeZone() {
        TimeZone defaultTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(defaultTimeZone);
        return defaultTimeZone;
    }
}