package io.getint.recruitment_task.httpclient;

import io.getint.recruitment_task.config.ConfigLoader;
import lombok.Getter;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Base64;

public class JiraHttpClient implements AutoCloseable {
    @Getter
    private final CloseableHttpClient httpClient;
    private final ConfigLoader.JiraApiConfig jiraApiConfig = ConfigLoader.getJiraApiConfig();

    public JiraHttpClient() {
        httpClient = HttpClients.custom()
                .addInterceptorFirst(this::addAuthorizationHeader)
                .addInterceptorLast(this::checkResponseStatus)
                .build();
    }

    public CloseableHttpResponse execute(HttpGet request) throws IOException {
        return getHttpClient().execute(request);
    }

    public void close() throws Exception {
        httpClient.close();
    }

    private void addAuthorizationHeader(HttpRequest request, HttpContext context) {
        String auth = jiraApiConfig.getUser() + ":" + jiraApiConfig.getApiKey();
        String authHeader = "Basic " + new String(Base64.getEncoder().encode(auth.getBytes()));
        request.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private void checkResponseStatus(HttpResponse response, HttpContext context) throws HttpException, IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        if (statusCode < 200 || statusCode >= 300) {
            throw new HttpException("Response failed: status=" + statusCode + ", body=" + (entity != null ? EntityUtils.toString(entity) : null));
        }
    }

}
