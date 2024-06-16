package io.getint.recruitment_task.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.httpclient.JiraHttpClient;
import io.getint.recruitment_task.util.JiraApiRequestQueryUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;


public class JiraApiService {

    private final Logger log = Logger.getLogger(JiraApiService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JiraApiService() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void fetchIssuesByProjectId(String projectId) {
        try (JiraHttpClient httpClient = new JiraHttpClient()) {
            HttpGet request = new HttpGet(JiraApiRequestQueryUtil.fetchFiveIssuesByProjectIdQueryUri(projectId));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONArray issues = new JSONObject(result).getJSONArray("issues");
                for (int i = 0; i < issues.length(); i++) {
                    log.info(issues.get(i).toString());
                }
            }
        } catch (Exception e) {
            log.severe(e.getCause().getMessage());
        }
    }

}
