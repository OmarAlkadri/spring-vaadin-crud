package org.vaadin.example.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.example.data.PersonRepository;

@Configuration
public class PersonServiceConfig {

    @Bean
    @ConditionalOnProperty(name = "db.enabled", havingValue = "true")
    PersonServiceSQL PersonServiceSQL(PersonRepository PersonRepository) {
        return new PersonServiceSQL(PersonRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "db.enabled", havingValue = "false", matchIfMissing = true)
    PersonServiceDummy PersonServiceDummy() {
        return new PersonServiceDummy();
    }
}
