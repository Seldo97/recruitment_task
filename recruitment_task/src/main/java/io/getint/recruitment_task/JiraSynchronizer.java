package io.getint.recruitment_task;

import io.getint.recruitment_task.service.JiraApiService;

public class JiraSynchronizer {
    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */
    public void moveTasksToOtherProject() throws Exception {
        JiraApiService jiraApiService = new JiraApiService();
        jiraApiService.fetchIssuesByProjectId("LBN");
    }
}
