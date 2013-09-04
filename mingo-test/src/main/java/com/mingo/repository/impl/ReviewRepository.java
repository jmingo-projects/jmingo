package com.mingo.repository.impl;

import static com.mingo.convert.ConversionUtils.convertList;
import static com.mingo.convert.ConversionUtils.getAsBasicDBList;
import static com.mingo.mongo.aggregation.AggregationOperators.GROUP;
import static com.mingo.mongo.aggregation.AggregationOperators.GT;
import static com.mingo.mongo.aggregation.AggregationOperators.MATCH;
import static com.mingo.mongo.aggregation.AggregationOperators.PROJECT;
import static com.mingo.mongo.aggregation.AggregationOperators.SUM;
import static com.mingo.mongo.aggregation.AggregationOperators.UNWIND;
import static com.mongodb.BasicDBObjectBuilder.start;
import com.google.common.collect.Lists;
import com.mingo.converter.ReviewCommonConverter;
import com.mingo.converter.ReviewSpecificConverter;
import com.mingo.domain.Author;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.api.IReviewRepository;
import com.mongodb.AggregationOutput;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
@Repository("reviewRepository")
public class ReviewRepository extends AbstractBaseRepository<String, Review> implements IReviewRepository {


    @Override
    protected Class<? extends Review> getDomainClass() {
        return Review.class;
    }

    private ReviewSpecificConverter reviewConverter = new ReviewSpecificConverter();

    @Override
    public Map<String, Integer> getCountByTags(ModerationStatus moderationStatus) {
        DBObject matchOperator = start().push(MATCH.getMongoName()).add("moderationStatus", moderationStatus != null ? moderationStatus.name() :
            ModerationStatus.STATUS_NOT_MODERATED.name()).get();
        DBObject unwindOperator = start().add(UNWIND.getMongoName(), "$tags").get();
        DBObject projectOperator = start().push(PROJECT.getMongoName()).add("moderationStatus", 1).add("tags", 1)
            .push("count").add("$add", new Integer[]{1}).pop().pop().get();
        DBObject groupOperator = start().push(GROUP.getMongoName()).add("_id", "$tags").push("totalCount")
            .add(SUM.getMongoName(), "$count").pop().pop().get();
        List<DBObject> operators = Lists.newArrayList(matchOperator, unwindOperator, projectOperator, groupOperator);
        Map<String, Integer> result = reviewConverter.convertCountByTags(
            getAsBasicDBList(aggregate("review", operators)));
        return result;
    }

    @Override
    public List<Review> getByTags(String... tags) {
        Criteria criteria = Criteria.where("tags").in(tags);
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    @Override
    public List<Review> getByRating(Float rating) {
        Criteria criteria = Criteria.where("rating").is(rating);
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    @Override
    public List<Review> getByModerationStatuses(Set<ModerationStatus> moderationStatuses) {
        Criteria criteria = Criteria.where("moderationStatus").in(moderationStatuses);
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    @Override
    public List<Review> getByModerationStatus(ModerationStatus moderationStatus) {
        Criteria criteria = Criteria.where("moderationStatus").is(moderationStatus);
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    @Override
    public List<Review> getByCreated(Date created) {
        DBObject matchOperator = start().push(MATCH.getMongoName()).add("created",
            start().add(GT.getMongoName(), created).get()).pop().get();
        List<DBObject> operators = Lists.newArrayList(matchOperator);
        return convertList(Review.class, getAsBasicDBList(aggregate("review", operators)),
            new ReviewCommonConverter());
    }

    @Override
    public List<Review> getByMultipleParameters(Map<String, Object> parameters) {
        Criteria criteria = new Criteria();
        if (!isParameterPresent(parameters, "statuses") && !isParameterPresent(parameters, "created")) {
            return getMongoTemplate().findAll(Review.class);
        }
        if (isParameterPresent(parameters, "statuses")) {
            criteria.and("moderationStatus").in((Set<ModerationStatus>) parameters.get("statuses"));
        }
        if (isParameterPresent(parameters, "created")) {
            criteria.and("created").gte(parameters.get("created"));
        }
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    @Override
    public List<Review> getByAuthor(Author author) {
        Criteria criteria = new Criteria();
        if (author.getName() != null) {
            criteria.and("author.name").is(author.getName());
        }
        if (author.getEmail() != null) {
            criteria.and("author.email").is(author.getEmail());
        }
        Query query = Query.query(criteria);
        return getMongoTemplate().find(query, Review.class);
    }

    private boolean isParameterPresent(Map<String, Object> parameters, String parameter) {
        return parameters.containsKey(parameter) && parameters.get(parameter) != null;
    }

    private AggregationOutput aggregate(String dbCollection, final List<DBObject> operators) {
        Assert.hasText(dbCollection, "collection name cannot be null");
        Assert.notEmpty(operators, "Operators cannot be null or empty. " +
            "Collection must contains at least one operator.");
        AggregationOutput aggregationOutput = getMongoTemplate().execute(dbCollection,
            new CollectionCallback<AggregationOutput>() {
                @Override
                public AggregationOutput doInCollection(DBCollection collection)
                    throws MongoException, DataAccessException {
                    DBObject firstOperator = operators.remove(FIRST_ELEMENT);
                    return collection.aggregate(firstOperator, operators.toArray(new DBObject[FIRST_ELEMENT]));
                }
            });
        return aggregationOutput;
    }
}
