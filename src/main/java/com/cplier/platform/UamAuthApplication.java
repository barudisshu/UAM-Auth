package com.cplier.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UamAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UamAuthApplication.class, args);
    }
}
