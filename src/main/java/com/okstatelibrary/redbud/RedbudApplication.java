package com.okstatelibrary.redbud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Redbud Spring Boot application.
 * <p>
 * This class is annotated with several Spring Boot features:
 * </p>
 * <ul>
 *   <li>{@link SpringBootApplication} - Indicates a Spring Boot application and enables auto-configuration.</li>
 *   <li>{@link EnableCaching} - Enables Spring's annotation-driven cache management capability.</li>
 *   <li>{@link EnableScheduling} - Enables support for scheduled tasks using the @Scheduled annotation.</li>
 * </ul>
 * 
 * @author Damith Perera
 */
@EnableCaching
@SpringBootApplication
@EnableScheduling
public class RedbudApplication {

    /**
     * The main method that serves as the application's entry point.
     * It bootstraps the Spring Boot application by invoking {@link SpringApplication#run(Class, String...)}.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(RedbudApplication.class, args);
    }
}
