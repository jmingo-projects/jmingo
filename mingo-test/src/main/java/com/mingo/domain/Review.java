package com.mingo.domain;


import com.mingo.annotation.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collectionName = Review.COLLECTION_NAME)
public class Review extends BaseDocument {

    public static final String COLLECTION_NAME = "review";
    private List<String> tags = new ArrayList<>();
    private ModerationStatus moderationStatus = ModerationStatus.STATUS_NOT_MODERATED;
    private Author author;
    private String identifier = UUID.randomUUID().toString();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
