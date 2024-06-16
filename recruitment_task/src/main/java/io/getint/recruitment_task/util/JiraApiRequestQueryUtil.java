package io.getint.recruitment_task.util;

import io.getint.recruitment_task.config.ConfigLoader;
import lombok.experimental.UtilityClass;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class JiraApiRequestQueryUtil {

    public static URIBuilder getDefaultUriBuilder() throws URISyntaxException {
        ConfigLoader.JiraApiConfig config = ConfigLoader.getJiraApiConfig();
        URIBuilder uriBuilder = new URIBuilder(config.getServer());
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        uriBuilder.setPath(config.getApiUri());
        return uriBuilder;
    }

    public static URI fetchFiveIssuesByProjectIdQueryUri(String projectId) throws URISyntaxException {
        URIBuilder uriBuilder = getDefaultUriBuilder();
        uriBuilder.setPathSegments(uriBuilder.getPath(), "search");
        uriBuilder.addParameter("jql", "project=" + projectId);
        uriBuilder.addParameter("maxResults", "5");
        return uriBuilder.build();
    }

}
