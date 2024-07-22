package com.example.task.integration.github;

import com.example.task.integration.GithubFacade;
import com.example.task.integration.dto.GithubDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
class GithubController {

    private static final class Routes {
        private static final String ROOT = "/api/github";
        private static final String GITHUB_USER_NAME = ROOT + "/{username}";
    }

    private final GithubFacade githubFacade;

    @GetMapping(Routes.GITHUB_USER_NAME)
    List<GithubDetails> findUserRepositories(@PathVariable String username) {
        return githubFacade.findRepositoryDetails(username);
    }
}