package io.getint.recruitment_task.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JiraDataUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private JiraDataUtils() {
    }

    /**
     * Sorts a list of Jira comments by their creation date.
     *
     * @param comments the JSONArray of comments to be sorted
     * @return a sorted list of comments as JSONObjects, or an empty list if the input is null
     */
    public static List<JSONObject> getSortedCommentsByCreated(JSONArray comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        List<JSONObject> commentList = new ArrayList<>();
        for (int i = 0; i < comments.length(); i++) {
            commentList.add(comments.getJSONObject(i));
        }
        commentList.sort(Comparator.comparing(c -> ZonedDateTime.parse(c.getString(Fields.Comment.CREATED), DATE_FORMAT)));
        return commentList;
    }

}
