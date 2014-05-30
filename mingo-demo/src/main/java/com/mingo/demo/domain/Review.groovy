package com.mingo.demo.domain


class Review extends BaseDocument {

    List<String> tags = new ArrayList<>();
    ModerationStatus moderationStatus = ModerationStatus.STATUS_NOT_MODERATED;
    Author author;

}
