package io.getint.recruitment_task.service;

import io.getint.recruitment_task.exception.JiraApiException;
import io.getint.recruitment_task.httpclient.JiraHttpClient;
import io.getint.recruitment_task.util.Fields;
import io.getint.recruitment_task.util.JiraApiRequestQueryUtil;
import io.getint.recruitment_task.util.JiraDataUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import static io.getint.recruitment_task.util.JiraApiRequestQueryUtil.*;

/**
 * Service class for interacting with Jira API to move issues between projects.
 * <p>
 * Author: Marcin Olek - marcinolek97@gmail.com
 */
public class JiraApiService {

    private final Logger log = Logger.getLogger(JiraApiService.class.getName());

    /**
     * Moves issues from the source project to the destination project.
     *
     * @param sourceProjectKey      the key of the source project
     * @param destinationProjectKey the key of the destination project
     * @return a JSONArray of created issues in the destination project
     * @throws JiraApiException if there is an error during the process
     */
    public JSONArray moveIssuesToOtherProject(String sourceProjectKey, String destinationProjectKey) throws JiraApiException {
        try (JiraHttpClient httpClient = new JiraHttpClient()) {
            log.info(String.format("### Start moving issues from project %S to %S ###", sourceProjectKey, destinationProjectKey));
            JSONArray sourceIssues = fetchIssuesByProjectKey(httpClient, sourceProjectKey);
//            log.info(sourceIssues.toString(4));
            JSONArray result = recreateIssuesInDestination(httpClient, sourceIssues, destinationProjectKey);
            log.info(String.format("### Issues moved from project %S to %S successfully ###", sourceProjectKey, destinationProjectKey));
            return result;
        } catch (Exception e) {
            throw new JiraApiException(e.getMessage(), e);
        }
    }

    /**
     * Recreates issues in the destination project.
     *
     * @param httpClient            the HTTP client to use
     * @param sourceIssues          the issues to recreate
     * @param destinationProjectKey the key of the destination project
     * @return a JSONArray of created issues in the destination project
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    public JSONArray recreateIssuesInDestination(JiraHttpClient httpClient, JSONArray sourceIssues, String destinationProjectKey) throws IOException, URISyntaxException {
        JSONArray createdIssues = new JSONArray();
        for (int i = 0; i < sourceIssues.length(); i++) {
            JSONObject sourceIssue = sourceIssues.getJSONObject(i);
            log.info(String.format("[%S] Moving issue id: %S", i, sourceIssue.get(Fields.Issue.ID)));
            JSONObject newIssue = createIssue(httpClient, createIssuePayload(sourceIssue, destinationProjectKey));
            if (!newIssue.isEmpty()) {
                log.info(String.format("Issue in project %S created: %S", destinationProjectKey, newIssue.toString(4)));
                recreateAdditionalDataInDestination(httpClient, newIssue, sourceIssue);
                createdIssues.put(newIssue);
                deleteIssueById(httpClient, sourceIssue.getString(Fields.Issue.ID));
            }
        }
        return createdIssues;
    }

    /**
     * Recreates additional data (comments, status) for the new issue in the destination project.
     *
     * @param httpClient  the HTTP client to use
     * @param newIssue    the new issue created in the destination project
     * @param sourceIssue the source issue from the source project
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private void recreateAdditionalDataInDestination(JiraHttpClient httpClient, JSONObject newIssue, JSONObject sourceIssue) throws IOException, URISyntaxException {
        String newIssueId = newIssue.getString(Fields.Issue.ID);
        String sourceIssueId = sourceIssue.getString(Fields.Issue.ID);
        JSONArray sourceComments = fetchIssueComments(httpClient, sourceIssueId);
        JSONArray newComments = addCommentsToIssue(httpClient, newIssueId, sourceComments);
        log.info(String.format("Comments [%S] moved to %S issue.", newComments.length(), newIssueId));
        String status = sourceIssue.getJSONObject(Fields.Issue.FIELDS).getJSONObject(Fields.Issue.STATUS).getString(Fields.Issue.NAME);
        transferIssueStatus(httpClient, newIssueId, status);
    }

    /**
     * Fetches issues by project key.
     *
     * @param httpClient       the HTTP client to use
     * @param sourceProjectKey the key of the source project
     * @return a JSONArray of issues
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONArray fetchIssuesByProjectKey(JiraHttpClient httpClient, String sourceProjectKey) throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(JiraApiRequestQueryUtil.fetchFiveIssuesByProjectKeyQueryUri(sourceProjectKey));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result).getJSONArray(Fields.SearchResult.ISSUES);
        }
    }

    /**
     * Creates a new issue in the destination project.
     *
     * @param httpClient   the HTTP client to use
     * @param issuePayload the payload for the new issue
     * @return the created issue as a JSONObject
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONObject createIssue(JiraHttpClient httpClient, JSONObject issuePayload) throws IOException, URISyntaxException {
        HttpPost postRequest = new HttpPost(createIssueUri());
        postRequest.setEntity(new StringEntity(issuePayload.toString()));
        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result);
        }
    }

    /**
     * Fetches comments for a given issue ID.
     *
     * @param httpClient the HTTP client to use
     * @param issueId    the ID of the issue
     * @return a JSONArray of comments
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONArray fetchIssueComments(JiraHttpClient httpClient, String issueId) throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(JiraApiRequestQueryUtil.fetchCommentsByIssueIdQueryUri(issueId));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result).getJSONArray(Fields.SearchResult.COMMENTS);
        }
    }

    /**
     * Adds comments to a new issue in the destination project.
     *
     * @param httpClient     the HTTP client to use
     * @param issueId        the ID of the new issue
     * @param sourceComments the comments from the source issue
     * @return a JSONArray of created comments
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONArray addCommentsToIssue(JiraHttpClient httpClient, String issueId, JSONArray sourceComments) throws IOException, URISyntaxException {
        JSONArray createdComments = new JSONArray();
        for (JSONObject sourceComment : JiraDataUtils.getSortedCommentsByCreated(sourceComments)) {
            JSONObject newComment = createCommentInIssue(httpClient, issueId, createCommentPayload(sourceComment));
            createdComments.put(newComment);
        }
        return createdComments;
    }

    /**
     * Creates a comment in a new issue in the destination project.
     *
     * @param httpClient     the HTTP client to use
     * @param issueId        the ID of the new issue
     * @param commentPayload the payload for the new comment
     * @return the created comment as a JSONObject
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONObject createCommentInIssue(JiraHttpClient httpClient, String issueId, JSONObject commentPayload) throws IOException, URISyntaxException {
        HttpPost request = new HttpPost(JiraApiRequestQueryUtil.createCommentByIssueIdUri(issueId));
        request.setEntity(new StringEntity(commentPayload.toString()));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result);
        }
    }

    /**
     * Transfers the status of a new issue to match the source issue.
     *
     * @param httpClient   the HTTP client to use
     * @param issueId      the ID of the new issue
     * @param targetStatus the target status to set
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private void transferIssueStatus(JiraHttpClient httpClient, String issueId, String targetStatus) throws IOException, URISyntaxException {
        JSONArray transitions = fetchAvailableTransitions(httpClient, issueId);
        for (int i = 0; i < transitions.length(); i++) {
            JSONObject transition = transitions.getJSONObject(i);
            if (transition.getJSONObject(Fields.Transition.TO).getString(Fields.Transition.NAME).equals(targetStatus)) {
                HttpPost request = new HttpPost(transferStatusByIssueIdUri(issueId));
                JSONObject payload = new JSONObject().put(
                        Fields.Transition.TRANSITION,
                        new JSONObject().put(Fields.Transition.ID, transition.getString(Fields.Transition.ID)));
                request.setEntity(new StringEntity(payload.toString()));
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    log.info(String.format("Status %S moved with issue %S", targetStatus, issueId));
                    return;
                }
            }
        }
    }

    /**
     * Fetches available transitions for a given issue ID.
     *
     * @param httpClient the HTTP client to use
     * @param issueId    the ID of the issue
     * @return a JSONArray of available transitions
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    private JSONArray fetchAvailableTransitions(JiraHttpClient httpClient, String issueId) throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(fetchTransitionsByIssueIdQueryUri(issueId));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result).getJSONArray(Fields.SearchResult.TRANSITIONS);
        }
    }

    /**
     * Deletes an issue by its ID.
     *
     * @param httpClient the HTTP client to use
     * @param issueId    the ID of the issue to delete
     * @throws URISyntaxException if the URI syntax is incorrect
     * @throws IOException        if an I/O error occurs
     */
    private void deleteIssueById(JiraHttpClient httpClient, String issueId) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(deleteIssueByIdUri(issueId));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            log.info(String.format("Issue %S deleted successfully", issueId));
        }
    }

}
