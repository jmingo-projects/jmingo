package com.mingo.domain.util;

import com.google.common.collect.Lists;
import com.mingo.domain.Author;
import com.mingo.domain.Comment;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
public class DomainTestBuilder {

    public static final String AUTHOR_NAME = "author name {}";
    public static final String AUTHOR_EMAIL = "author email {}";
    public static final String REVIEW_TEXT = "text {}";
    public static final String COMMENT_BODY = "comment body {}";
    public static final int TAGS_COUNT = 10;


    public static List<Review> createReviews(int count) {
        List<Review> reviews = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            reviews.add(createReview(i));
        }
        return reviews;
    }

    public static Review createReview(int prefix) {
        Review review = new Review();
        review.setAuthor(createAuthor(prefix));
        review.setModerationStatus(ModerationStatus.values()[random(0, ModerationStatus.values().length - 1)]);
        review.setCreated(createDateAndAddYear(prefix));
        review.setText(MessageFormatter.format(REVIEW_TEXT, prefix).getMessage());
        review.setRating(Float.valueOf(prefix));
        review.addComment(createComment(prefix));
        for (int i = 0; i < random(1, TAGS_COUNT); i++) {
            review.addTag(TAGS.get(random(0, TAGS.size() - 1)));
            i++;
        }
        return review;
    }


    public static Comment createComment(int prefix) {
        Comment comment = new Comment();
        comment.setModerationStatus(ModerationStatus.values()[random(0, ModerationStatus.values().length - 1)]);
        comment.setId(UUID.randomUUID().toString());
        comment.setAuthor(createAuthor(prefix));
        comment.setCreated(createDateAndAddYear(prefix));
        comment.setCommentBody(MessageFormatter.format(COMMENT_BODY, prefix).getMessage());
        return comment;
    }

    public static Author createAuthor(int prefix) {
        return new Author(MessageFormatter.format(AUTHOR_NAME, prefix).getMessage(),
            MessageFormatter.format(AUTHOR_EMAIL, prefix).getMessage());
    }

    /**
     * Creates date.
     *
     * @param year the amount to add, may be negative
     * @return the new date object with the amount added
     */
    public static Date createDateAndAddYear(int year) {
        return DateUtils.addYears(new Date(), year);
    }

    public static int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static final List<String> TAGS = Lists.newArrayList(
        "3D",
        "AJAX",
        "animation",
        "Apps",
        "Best Of",
        "Best practices",
        "Calendars",
        "Calligraphy",
        "cartoons",
        "carts",
        "CG",
        "charts",
        "Cheat Sheets",
        "Christmas",
        "Clients",
        "CMS",
        "Community",
        "CSS",
        "Design",
        "development",
        "E-Commerce",
        "Global Web Design",
        "Google",
        "optimization",
        "Performance",
        "principles",
        "Process",
        "SEO",
        "software",
        "Tools",
        "Web"
    );

}
