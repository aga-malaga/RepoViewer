package com.example.task.integration.dto;

public record GithubBranchResponse(
        String name,
        Commit commit
) {

    public record Commit(
            String sha
    ) {
    }
}