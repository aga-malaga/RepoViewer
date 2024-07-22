package com.example.task.integration.dto;

public record GithubRepoResponse(
        String name,
        String branches_url,
        boolean fork,
        Owner owner
) {

    public record Owner(
            String login
    ) {
    }
}