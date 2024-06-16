package io.getint.recruitment_task.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Objects;

@UtilityClass
public class ConfigLoader {

    private static final String JIRA_API_CONFIG_PATH = "jira-api-config.yml";
    private static JiraApiConfig jiraApiConfig;

    public static JiraApiConfig loadJiraApiConfig() {
        if (Objects.isNull(jiraApiConfig)) {
            Yaml yaml = new Yaml(new Constructor(JiraApiConfig.class));
            InputStream inputStream = ConfigLoader.class
                    .getClassLoader()
                    .getResourceAsStream(JIRA_API_CONFIG_PATH);
            jiraApiConfig = yaml.load(inputStream);
        }
        return jiraApiConfig;
    }

    public static JiraApiConfig getJiraApiConfig() {
        return Objects.isNull(jiraApiConfig) ? loadJiraApiConfig() : jiraApiConfig;
    }

    @Getter
    @Setter
    public static class JiraApiConfig {
        private String server;
        private String apiUri;
        private String apiKey;
        private String user;

        private JiraApiConfig() {
        }
    }

}
