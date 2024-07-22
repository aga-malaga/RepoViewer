package com.example.task.integration;

import com.example.task.integration.dto.GithubDetails;

import java.util.List;

public interface GithubFacade {
    List<GithubDetails> findRepositoryDetails(String username);
}