package io.getint.recruitment_task.util;

/**
 * Utility class containing constant field names for Jira API JSON objects.
 */
public class Fields {

    private Fields() {
    }

    public static final String PROJECT = "project";
    public static final String KEY = "key";

    public static class SearchResult {
        public static final String ISSUES = "issues";
        public static final String COMMENTS = "comments";
        public static final String TRANSITIONS = "transitions";
    }

    public static class Issue {
        public static final String ID = "id";
        public static final String FIELDS = "fields";
        public static final String SUMMARY = "summary";
        public static final String DESCRIPTION = "description";
        public static final String PRIORITY = "priority";
        public static final String NAME = "name";
        public static final String ISSUE_TYPE = "issuetype";
        public static final String STATUS = "status";
    }

    public static class Comment {
        public static final String ID = "id";
        public static final String BODY = "body";
        public static final String AUTHOR = "author";
        public static final String CREATED = "created";
    }

    public static class Transition {
        public static final String TRANSITION = "transition";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String TO = "to";
    }

    public static class IssueType {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

}
