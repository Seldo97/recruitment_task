package io.getint.recruitment_task.util;

import io.getint.recruitment_task.config.ConfigLoader;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for constructing Jira API request URIs and payloads.
 */
public class JiraApiRequestQueryUtil {

    private JiraApiRequestQueryUtil() {
    }

    /**
     * Creates a default URIBuilder configured with Jira API server and base API URI.
     *
     * @return a configured URIBuilder instance
     * @throws URISyntaxException if the URI syntax is incorrect
     */
    public static URIBuilder getDefaultUriBuilder() throws URISyntaxException {
        ConfigLoader.JiraApiConfig config = ConfigLoader.getJiraApiConfig();
        URIBuilder uriBuilder = new URIBuilder(config.getServer());
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        uriBuilder.setPath(config.getApiUri());
        return uriBuilder;
    }

    public static URI fetchFiveIssuesByProjectKeyQueryUri(String projectKey) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "search");
        uriBuilder.addParameter("jql", "project=" + projectKey);
        uriBuilder.addParameter("maxResults", "5");
        return uriBuilder.build();
    }

    public static URI fetchTransitionsByIssueIdQueryUri(String issueId) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issue", issueId, "transitions");
        return uriBuilder.build();
    }

    public static URI fetchIssueTypesByProjectKeyQueryUri(String projectKey) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issue", "createmeta", projectKey, "issuetypes");
        return uriBuilder.build();
    }

    public static URI fetchCommentsByIssueIdQueryUri(String issueId) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issue", issueId, "comment");
        return uriBuilder.build();
    }

    public static URI createCommentByIssueIdUri(String issueId) throws URISyntaxException {
        return fetchCommentsByIssueIdQueryUri(issueId);
    }

    public static URI transferStatusByIssueIdUri(String issueId) throws URISyntaxException {
        return fetchTransitionsByIssueIdQueryUri(issueId);
    }

    public static URI createIssueUri() throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issue");
        return uriBuilder.build();
    }

    public static URI createIssueTypeUri() throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issuetype");
        return uriBuilder.build();
    }

    public static URI deleteIssueByIdUri(String issueId) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "issue", issueId);
        return uriBuilder.build();
    }

    public static JSONObject createIssuePayload(JSONObject sourceIssue, String projectKey) {
        String summary = sourceIssue.getJSONObject(Fields.Issue.FIELDS).getString(Fields.Issue.SUMMARY);
        String description = sourceIssue.getJSONObject(Fields.Issue.FIELDS).getString(Fields.Issue.DESCRIPTION);
        String priority = sourceIssue.getJSONObject(Fields.Issue.FIELDS).getJSONObject(Fields.Issue.PRIORITY).getString(Fields.Issue.NAME);
        String issueTypeName = sourceIssue.getJSONObject(Fields.Issue.FIELDS).getJSONObject(Fields.Issue.ISSUE_TYPE).getString(Fields.IssueType.NAME);

        return new JSONObject()
                .put(Fields.Issue.FIELDS, new JSONObject()
                        .put(Fields.PROJECT, new JSONObject().put(Fields.KEY, projectKey))
                        .put(Fields.Issue.SUMMARY, summary)
                        .put(Fields.Issue.DESCRIPTION, description)
                        .put(Fields.Issue.PRIORITY, new JSONObject().put(Fields.Issue.NAME, priority))
                        .put(Fields.Issue.ISSUE_TYPE, new JSONObject().put(Fields.IssueType.NAME, issueTypeName)));
    }

    public static JSONObject createCommentPayload(JSONObject sourceComment) {
        String body = sourceComment.getString(Fields.Comment.BODY);
        JSONObject author = sourceComment.getJSONObject(Fields.Comment.AUTHOR);

        return new JSONObject()
                .put(Fields.Comment.BODY, body)
                .put(Fields.Comment.AUTHOR, author);
    }

    public static JSONObject createIssueTypePayload(JSONObject sourceIssueType, String projectKey) {
        String name = sourceIssueType.getString(Fields.IssueType.NAME);
        String description = sourceIssueType.getString(Fields.IssueType.DESCRIPTION);

        return new JSONObject()
                .put(Fields.PROJECT, new JSONObject().put(Fields.KEY, projectKey))
                .put(Fields.IssueType.NAME, name)
                .put(Fields.IssueType.DESCRIPTION, description);
    }

}
