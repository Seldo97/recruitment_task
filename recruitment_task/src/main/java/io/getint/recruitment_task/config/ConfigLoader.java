package io.getint.recruitment_task.config;


import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Objects;

/**
 * Utility class for loading Jira API configuration from a YAML file.
 */
public class ConfigLoader {

    private ConfigLoader() {
    }

    private static final String JIRA_API_CONFIG_PATH = "jira-api-config.yml";
    private static JiraApiConfig jiraApiConfig;

    /**
     * Loads the Jira API configuration from the YAML file.
     *
     * @return the loaded JiraApiConfig object
     */
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

    /**
     * Gets the Jira API configuration. Loads it if not already loaded.
     *
     * @return the JiraApiConfig object
     */
    public static JiraApiConfig getJiraApiConfig() {
        return Objects.isNull(jiraApiConfig) ? loadJiraApiConfig() : jiraApiConfig;
    }

    /**
     * Inner class representing the Jira API configuration.
     */
    public static class JiraApiConfig {
        private String server;
        private String apiUri;
        private String apiKey;
        private String user;

        private JiraApiConfig() {
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getApiUri() {
            return apiUri;
        }

        public void setApiUri(String apiUri) {
            this.apiUri = apiUri;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

}
