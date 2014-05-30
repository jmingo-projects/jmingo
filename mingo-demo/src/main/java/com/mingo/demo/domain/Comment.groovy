package com.mingo.demo.domain


class Comment {

    ModerationStatus moderationStatus = ModerationStatus.STATUS_NOT_MODERATED;
    Author author;
    Date created = new Date();
    String commentBody;

    Comment() {
    }

    Comment(String commentBody, ModerationStatus moderationStatus) {
        this.commentBody = commentBody
        this.moderationStatus = moderationStatus
    }
}
