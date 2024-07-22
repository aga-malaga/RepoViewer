package com.example.task.integration;

import java.net.http.HttpResponse;

public interface HttpClientFacade {
    HttpResponse<String> getRequest(String path, String[] headers);
}