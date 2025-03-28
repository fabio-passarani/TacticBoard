package com.tacticboard.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tacticboard"})
@EntityScan(basePackages = {"com.tacticboard.core.model"})
@EnableJpaRepositories(basePackages = {"com.tacticboard.persistence.repository"})
@EnableAsync
@EnableScheduling
public class TacticBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(TacticBoardApplication.class, args);
    }
}