package com.example.task.integration.github;

import com.example.task.common.exception.GithubException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.mapping.JsonMapper;
import com.example.task.integration.GithubFacade;
import com.example.task.integration.HttpClientFacade;
import com.example.task.integration.dto.GithubBranchResponse;
import com.example.task.integration.dto.GithubDetails;
import com.example.task.integration.dto.GithubRepoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class GithubServiceImpl implements GithubFacade {

    private static final class Endpoints {
        private static final String ROOT = "https://api.github.com";
        private static final String FIND_USER_FORMAT = ROOT + "/users/%s";
        private static final String LIST_REPOSITORIES_FORMAT = ROOT + "/users/%s/repos";
    }

    private static final String NOT_FOUND_MESSAGE_FORMAT = "Username %s not found";
    private static final String[] HEADERS = {HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE};

    private final HttpClientFacade httpClientFacade;
    private final JsonMapper jsonMapper;

    @Override
    public List<GithubDetails> findRepositoryDetails(String username) {
        verifyIfUserExists(username);

        List<GithubRepoResponse> repositories = callGithubForRepositoryList(username).stream()
                .filter(GithubServiceImpl::isNotFork)
                .toList();

        return findDetails(repositories);
    }

    private void verifyIfUserExists(String username) {
        HttpResponse<String> response = httpClientFacade.getRequest(
                Endpoints.FIND_USER_FORMAT.formatted(username), HEADERS);

        if (isNotFound(response)) {
            throw new NotFoundException(NOT_FOUND_MESSAGE_FORMAT.formatted(username), response.statusCode());
        } else if (!isSuccess(response)) {
            throw new GithubException(response.body(), response.statusCode());
        }
    }

    private List<GithubDetails> findDetails(List<GithubRepoResponse> repoResponses) {
        List<GithubDetails> details = new ArrayList<>();

        for (GithubRepoResponse response : repoResponses) {
            List<GithubBranchResponse> branchResponses = callGithubForBranchList(response);
            GithubDetails githubDetails = new GithubDetails(response.name(), response.owner().login(), branchResponses);
            details.add(githubDetails);
        }
        return details;
    }

    private List<GithubRepoResponse> callGithubForRepositoryList(String username) {
        HttpResponse<String> response = httpClientFacade.getRequest(
                Endpoints.LIST_REPOSITORIES_FORMAT.formatted(username), HEADERS);

        if (isSuccess(response)) {
            return mapToRepoResponse(response);
        } else {
            throw new GithubException(response.body(), response.statusCode());
        }
    }

    private List<GithubBranchResponse> callGithubForBranchList(GithubRepoResponse repoResponse) {
        HttpResponse<String> response = httpClientFacade.getRequest(getPath(repoResponse), HEADERS);

        if (isSuccess(response)) {
            return mapToBranchResponse(response);
        } else {
            throw new GithubException(response.body(), response.statusCode());
        }
    }

    private static String getPath(GithubRepoResponse repoResponse) {
        return repoResponse.branches_url().replace("{/branch}", "");
    }

    private static boolean isSuccess(HttpResponse<String> response) {
        return HttpStatus.valueOf(response.statusCode()).is2xxSuccessful();
    }

    private static boolean isNotFound(HttpResponse<String> response) {
        return response.statusCode() == HttpStatus.NOT_FOUND.value();
    }

    private static boolean isNotFork(GithubRepoResponse repo) {
        return !repo.fork();
    }

    private List<GithubRepoResponse> mapToRepoResponse(HttpResponse<String> response) {
        return jsonMapper.toObject(response.body(), new TypeReference<>() {
        });
    }

    private List<GithubBranchResponse> mapToBranchResponse(HttpResponse<String> response) {
        return jsonMapper.toObject(response.body(), new TypeReference<>() {
        });
    }
}