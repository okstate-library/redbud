package com.okstatelibrary.redbud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Redbud application. 
 */

@EnableCaching
@SpringBootApplication
@EnableScheduling
public class RedbudApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbudApplication.class, args);
    }
}
