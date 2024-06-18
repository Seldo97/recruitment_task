package io.getint.recruitment_task.httpclient;

import io.getint.recruitment_task.config.ConfigLoader;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * A custom HTTP client for interacting with Jira API, utilizing
 * Apache HttpClient with pre-configured authentication and response checking.
 */
public class JiraHttpClient implements AutoCloseable {

    private final CloseableHttpClient httpClient;
    private final ConfigLoader.JiraApiConfig jiraApiConfig = ConfigLoader.getJiraApiConfig();

    /**
     * Constructs a new JiraHttpClient with authentication and response validation.
     */
    public JiraHttpClient() {
        httpClient = HttpClients.custom()
                .addInterceptorFirst(this::addAuthorizationHeader)
                .addInterceptorLast(this::checkResponseStatus)
                .build();
    }

    public CloseableHttpResponse execute(HttpGet request) throws IOException {
        return getHttpClient().execute(request);
    }

    public CloseableHttpResponse execute(HttpPost request) throws IOException {
        return getHttpClient().execute(request);
    }

    public CloseableHttpResponse execute(HttpPut request) throws IOException {
        return getHttpClient().execute(request);
    }

    public CloseableHttpResponse execute(HttpDelete request) throws IOException {
        return getHttpClient().execute(request);
    }

    public void close() throws Exception {
        httpClient.close();
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Adds an authorization header to the HTTP request.
     *
     * @param request the HTTP request to modify
     * @param context the context of the HTTP request
     */
    private void addAuthorizationHeader(HttpRequest request, HttpContext context) {
        String auth = jiraApiConfig.getUser() + ":" + jiraApiConfig.getApiKey();
        String authHeader = "Basic " + new String(Base64.getEncoder().encode(auth.getBytes()));
        request.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    /**
     * Checks the status of the HTTP response and throws an exception if it indicates failure.
     *
     * @param response the HTTP response to check
     * @param context  the context of the HTTP request
     * @throws HttpException if the response status code indicates failure
     * @throws IOException   if an I/O error occurs
     */
    private void checkResponseStatus(HttpResponse response, HttpContext context) throws HttpException, IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        if (statusCode < 200 || statusCode >= 300) {
            throw new HttpException(String.format("Response failed: status=%S, body=%S", statusCode, (entity != null ? EntityUtils.toString(entity) : null)));
        }
    }

}
