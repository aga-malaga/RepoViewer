package com.example.task.integration.github;

import com.example.task.common.mapping.JsonMapper;
import com.example.task.integration.GithubFacade;
import com.example.task.integration.HttpClientFacade;

import static org.mockito.Mockito.mock;

class GithubFacadeTest {

    private final HttpClientFacade httpClientFacade = mock();
    private final JsonMapper jsonMapper = mock();
    private final GithubFacade systemUnderTest = new GithubConfiguration().githubFacade(httpClientFacade, jsonMapper);


}