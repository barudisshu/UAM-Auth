package com.cplier.platform.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.cplier.platform.repository")
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.cplier.platform.entity"})
public class JpaConfiguration {

}