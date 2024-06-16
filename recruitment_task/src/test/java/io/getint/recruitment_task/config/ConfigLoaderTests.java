package io.getint.recruitment_task.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConfigLoaderTests {

    @Test
    public void shouldLoadJiraApiConfigWithoutThrow() {
        try {
            ConfigLoader.loadJiraApiConfig();
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void shouldBindJiraApiConfigApiUrlField() {
        ConfigLoader.JiraApiConfig jiraApiConfig = ConfigLoader.loadJiraApiConfig();
        assertEquals("/rest/agile/1.0", jiraApiConfig.getApiUri());
    }

}
