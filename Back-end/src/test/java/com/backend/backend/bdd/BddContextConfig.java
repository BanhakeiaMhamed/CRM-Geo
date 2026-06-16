package com.backend.backend.bdd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BddContextConfig {

    @Bean
    BddContext bddContext() {
        return new BddContext();
    }
}
