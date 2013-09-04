package com.mingo.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Document(collection = Review.COLLECTION_NAME)
@CompoundIndexes({

    @CompoundIndex(name = "moderationStatus_created_index",
        def = "{'moderationStatus': 1, 'created' : 1}"),
    @CompoundIndex(name = "author_name_index", def = "{'author.authorName' : 1 }"),
    @CompoundIndex(name = "author_email_index", def = "{'author.email' : 1 }"),
})
public class Review extends Domain {

    public static final String COLLECTION_NAME = "review";

    private Author author;

    @Indexed
    private Date created;

    private String text;

    @Indexed
    private ModerationStatus moderationStatus;

    private List<Comment> comments = Collections.emptyList();

    @Indexed(sparse = true)
    private Set<String> tags = Collections.emptySet();

    @Indexed
    private Float rating;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public void addComment(Comment comment) {
        if (CollectionUtils.isEmpty(comments)) {
            comments = Lists.newArrayList();
        }
        comments.add(comment);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        if (CollectionUtils.isEmpty(tags)) {
            tags = Sets.newHashSet();
        }
        tags.add(tag);
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Review)) {
            return false;
        }

        Review that = (Review) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(that))
            .append(moderationStatus, that.moderationStatus)
            .append(author, that.author)
            .append(created, that.created)
            .append(text, that.text)
            .append(comments, that.comments)
            .append(tags, that.tags)
            .append(rating, that.rating)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(moderationStatus)
            .append(author)
            .append(created)
            .append(text)
            .append(comments)
            .append(tags)
            .append(rating)
            .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("moderationStatus", moderationStatus)
            .append("author", author)
            .append("created", created)
            .append("text", text)
            .append("comments", comments)
            .append("tags", tags)
            .append("rating", rating)
            .toString();
    }
}
