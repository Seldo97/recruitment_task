package io.getint.recruitment_task;

import io.getint.recruitment_task.config.ConfigLoader;

public class RecruitmentTaskApplication {

    public static void main(String[] args) {
        ConfigLoader.loadJiraApiConfig();
    }

}
