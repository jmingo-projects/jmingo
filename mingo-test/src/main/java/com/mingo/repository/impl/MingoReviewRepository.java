package com.mingo.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mingo.core.MingoTemplate;
import com.mingo.domain.Author;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.api.IReviewRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@Repository("mingoReviewRepository")
public class MingoReviewRepository implements IReviewRepository {


    @Autowired
    private MingoTemplate mingoTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String DB_COLLECTION_NAME = "mingotest.review.";


    protected Class getDomainClass() {
        return Review.class;
    }

    @Override
    public java.lang.String insert(com.mingo.domain.Review review) {
        review.setId(UUID.randomUUID().toString());
        mongoTemplate.insert(review);
        return review.getId();
    }

    @Override
    public com.mingo.domain.Review findById(java.lang.String id) {
        Assert.hasText(id, "ReviewRepository::findById(); is cannot be null");
        return mingoTemplate.queryForObject(DB_COLLECTION_NAME + "getById", Review.class, ImmutableMap.<String, Object>of("id", id));
    }

    @Override
    public List<com.mingo.domain.Review> findAll() {
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getAll", Review.class);
    }

    @Override
    public void update(com.mingo.domain.Review object) {
    }

    @Override
    public void delete(com.mingo.domain.Review object) {
    }

    @Override
    public Map<String, Integer> getCountByTags(ModerationStatus moderationStatus) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("moderationStatus", moderationStatus);
        return mingoTemplate.queryForObject(DB_COLLECTION_NAME + "getCountByTags", Map.class, parameters);
    }

    @Override
    public List<Review> getByTags(String... tags) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("tags", tags);
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByTags", Review.class,
            parameters);
    }

    @Override
    public List<Review> getByRating(Float rating) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("rating", rating);
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByRating", Review.class,
            parameters);
    }

    @Override
    public List<Review> getByModerationStatuses(Set<ModerationStatus> moderationStatuses) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", moderationStatuses);
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByModerationStatuses", Review.class,
            parameters);
    }

    @Override
    public List<Review> getByModerationStatus(ModerationStatus moderationStatus) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("moderationStatus", moderationStatus);
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByModerationStatus", Review.class,
            parameters);
    }

    @Override
    public List<Review> getByCreated(Date created) {
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByCreated", Review.class,
            ImmutableMap.<String, Object>of("created", created));
    }

    @Override
    public List<Review> getByMultipleParameters(Map<String, Object> parameters) {
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByMultipleParameters", Review.class,
            parameters);
    }

    @Override
    public List<Review> getByAuthor(Author author) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("name", author.getName());
        parameters.put("email", author.getEmail());
        return mingoTemplate.queryForList(DB_COLLECTION_NAME + "getByAuthor", Review.class,
            parameters);

    }
}
