package com.example.task.integration.github;

import com.example.task.common.mapping.JsonMapper;
import com.example.task.integration.GithubFacade;
import com.example.task.integration.HttpClientFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GithubConfiguration {

    @Bean
    GithubFacade githubFacade(HttpClientFacade httpClientFacade, JsonMapper jsonMapper) {
        return new GithubServiceImpl(httpClientFacade, jsonMapper);
    }
}