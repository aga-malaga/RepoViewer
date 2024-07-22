package com.example.task.common.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    JsonMapper jsonMapper() {
        return new JsonMapper();
    }
}