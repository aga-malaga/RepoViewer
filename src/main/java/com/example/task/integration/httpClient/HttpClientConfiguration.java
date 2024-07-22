package com.example.task.integration.httpClient;

import com.example.task.integration.HttpClientFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
class HttpClientConfiguration {

    private static final Duration CONNECT_TIMEOUT_DURATION = Duration.ofSeconds(20);

    @Bean
    HttpClientFacade httpClientFacade(HttpClient httpClient) {
        return new HttpClientServiceImpl(httpClient);
    }

    @Bean
    HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT_DURATION)
                .build();
    }
}