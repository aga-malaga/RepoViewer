package com.example.task.integration.github;

import com.example.task.common.exception.GithubException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.mapping.JsonMapper;
import com.example.task.integration.GithubFacade;
import com.example.task.integration.HttpClientFacade;
import com.example.task.integration.dto.GithubBranchResponse;
import com.example.task.integration.dto.GithubDetails;
import com.example.task.integration.dto.GithubRepoResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GithubFacadeTest {

    private final HttpClientFacade httpClientFacade = mock();
    private final JsonMapper jsonMapper = mock();
    private final GithubFacade systemUnderTest = new GithubConfiguration().githubFacade(httpClientFacade, jsonMapper);

    @Nested
    class FindRepositoryDetails {

        @Test
        void shouldThrowNotFoundExceptionWhenResponseFromHttpClientNotFound() {
            //given
            String username = "user";

            HttpResponse<String> response = mock();
            given(httpClientFacade.getRequest(any(), any()))
                    .willReturn(response);

            given(response.statusCode())
                    .willReturn(HttpStatus.NOT_FOUND.value());

            //when
            NotFoundException exception = catchThrowableOfType(
                    () -> systemUnderTest.findRepositoryDetails(username), NotFoundException.class);

            //then
            assertThat(exception)
                    .extracting(NotFoundException::getStatus)
                    .isEqualTo(404);
            assertThat(exception.getMessage())
                    .isEqualTo("Username: user not found");
        }

        @MethodSource("provideHttpStatuses")
        @ParameterizedTest
        void shouldThrowGithubExceptionWhenResponseFromHttpClientNotSuccess(HttpStatus status) {
            //given
            String username = "user";

            HttpResponse<String> response = mock();
            given(httpClientFacade.getRequest(any(), any()))
                    .willReturn(response);

            given(response.statusCode())
                    .willReturn(status.value());

            //when
            GithubException exception = catchThrowableOfType(
                    () -> systemUnderTest.findRepositoryDetails(username), GithubException.class);

            //then
            assertThat(exception)
                    .extracting(GithubException::getStatus)
                    .isEqualTo(status.value());
        }

        @Test
        void shouldReturnListOfGithubDetails() {
            String username = "user";

            HttpResponse<String> userExistsResponse = mock();
            HttpResponse<String> repoResponse = mock();
            given(httpClientFacade.getRequest(any(), any()))
                    .willReturn(userExistsResponse)
                    .willReturn(repoResponse);

            given(userExistsResponse.statusCode())
                    .willReturn(HttpStatus.valueOf(200).value());

            given(repoResponse.statusCode())
                    .willReturn(HttpStatus.valueOf(200).value());

            HttpResponse<String> branchResponse = mock();
            HttpResponse<String> branchResponse2 = mock();
            HttpResponse<String> branchResponse3 = mock();
            given(httpClientFacade.getRequest(eq("branch1_url"), any()))
                    .willReturn(branchResponse);
            given(httpClientFacade.getRequest(eq("branch2_url"), any()))
                    .willReturn(branchResponse2);
            given(httpClientFacade.getRequest(eq("branch3_url"), any()))
                    .willReturn(branchResponse3);

            given(branchResponse.statusCode())
                    .willReturn(HttpStatus.valueOf(200).value());
            given(branchResponse2.statusCode())
                    .willReturn(HttpStatus.valueOf(200).value());
            given(branchResponse3.statusCode())
                    .willReturn(HttpStatus.valueOf(200).value());

            GithubRepoResponse githubRepoResponse = new GithubRepoResponse(
                    "name", "branch1_url{/branch}", true, new GithubRepoResponse.Owner(username));
            GithubRepoResponse githubRepoResponse2 = new GithubRepoResponse(
                    "name2", "branch2_url{/branch}", false, new GithubRepoResponse.Owner(username));
            GithubRepoResponse githubRepoResponse3 = new GithubRepoResponse(
                    "name3", "branch3_url{/branch}", false, new GithubRepoResponse.Owner(username));

            GithubBranchResponse githubBranchResponse = new GithubBranchResponse(
                    "branchName", new GithubBranchResponse.Commit("1234"));
            GithubBranchResponse githubBranchResponse2 = new GithubBranchResponse(
                    "branchName2", new GithubBranchResponse.Commit("12345"));
            GithubBranchResponse githubBranchResponse3 = new GithubBranchResponse(
                    "branchName3", new GithubBranchResponse.Commit("123456"));
            GithubBranchResponse githubBranchResponse4 = new GithubBranchResponse(
                    "branchName4", new GithubBranchResponse.Commit("1234567"));

            given(jsonMapper.toObject(any(), any()))
                    .willReturn(List.of(githubRepoResponse, githubRepoResponse2, githubRepoResponse3))
                    .willReturn(List.of(githubBranchResponse2))
                    .willReturn(List.of(githubBranchResponse3, githubBranchResponse4));

            //when
            List<GithubDetails> actual = systemUnderTest.findRepositoryDetails(username);

            //then
            assertThat(actual)
                    .extracting(GithubDetails::repositoryName,
                            GithubDetails::login,
                            GithubDetails::branches)
                    .containsExactlyInAnyOrder(
                            tuple("name2", "user", List.of(
                                    new GithubBranchResponse("branchName2", new GithubBranchResponse.Commit("12345")))),
                            tuple("name3", "user", List.of(
                                    new GithubBranchResponse("branchName3", new GithubBranchResponse.Commit("123456")),
                                    new GithubBranchResponse("branchName4", new GithubBranchResponse.Commit("1234567")))));
        }

        private static Stream<Arguments> provideHttpStatuses() {
            return Stream.of(
                    Arguments.of(HttpStatus.BAD_GATEWAY),
                    Arguments.of(HttpStatus.BAD_REQUEST),
                    Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR),
                    Arguments.of(HttpStatus.FORBIDDEN),
                    Arguments.of(HttpStatus.METHOD_NOT_ALLOWED),
                    Arguments.of(HttpStatus.I_AM_A_TEAPOT));
        }
    }
}