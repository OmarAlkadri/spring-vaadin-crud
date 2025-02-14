package org.vaadin.example;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.vaadin.example.data.PersonRepository;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "spring-vaadin-crud")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(
            DataSource dataSource,
            SqlInitializationProperties properties,
            PersonRepository repository,
            Environment environment) {

        boolean dbEnabled = Boolean.parseBoolean(environment.getProperty("db.enabled", "false"));

        if (!dbEnabled) {
            return null;
        }

        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                return repository.count() == 0L && super.initializeDatabase();
            }
        };
    }
}
