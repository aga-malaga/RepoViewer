package com.example.task.integration.dto;

import java.util.List;

public record GithubDetails(
        String repositoryName,
        String login,
        List<GithubBranchResponse> branches
) {
}