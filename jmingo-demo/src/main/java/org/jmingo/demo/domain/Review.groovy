package org.jmingo.demo.domain

import com.fasterxml.jackson.annotation.JsonIgnore


class Review extends BaseDocument {

    List<String> tags = new ArrayList<>();
    ModerationStatus moderationStatus = ModerationStatus.STATUS_NOT_MODERATED;
    Author author;
    List<Comment> comments;
    List<Rating> ratings;
    String text;
    String title;
    Date created = new Date();
    @JsonIgnore
    Integer commentsCount;

}
