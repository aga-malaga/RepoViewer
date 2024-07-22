package com.example.task.integration.httpClient;

import com.example.task.integration.HttpClientFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
class HttpClientServiceImpl implements HttpClientFacade {

    private final HttpClient httpClient;

    @Override
    public HttpResponse<String> getRequest(String path, String[] headers) {
        HttpRequest request = newBuilder(path)
                .headers(headers)
                .build();

        return send(request);
    }

    private static HttpRequest.Builder newBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(path))
                .GET();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }
}