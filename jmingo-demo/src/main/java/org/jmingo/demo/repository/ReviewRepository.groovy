package org.jmingo.demo.repository

import com.google.common.collect.Maps
import org.jmingo.mongo.index.Index
import org.jmingo.demo.domain.ModerationStatus
import org.jmingo.demo.domain.Review
import org.jmingo.demo.repository.api.IBaseRepository
import org.springframework.stereotype.Repository

import javax.annotation.PostConstruct

import static org.jmingo.util.DocumentUtils.getCollectionName

@Repository
class ReviewRepository extends AbstractRepository<Review> implements IBaseRepository<String, Review> {

    private indexes;

    @PostConstruct
    public void init() {
        indexes = [
                Index.builder().name("moderationStatus_index").key("moderationStatus").build()
        ]
        createIndexes()
    }

    private def createIndexes() {
        indexes.each { getJMingoTemplate().ensureIndex(Review.class, it) }
    }

    List<Review> getByAuthor(String name, String email) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("name", name);
        parameters.put("email", email);
        String collectionName = getCollectionName(Review.class);
        return jMingoTemplate.queryForList(collectionName + ".getByAuthor", Review.class, parameters);
    }

    Map<String, Integer> getTagsCount(def statuses) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", statuses);
        String collectionName = getCollectionName(Review.class);
        return jMingoTemplate.queryForObject(collectionName + ".getTagsCount", Map.class, parameters);
    }

    List<Review> getByModerationStatus(ModerationStatus status, int limit, int skip) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("status", status);
        parameters.put("limit", limit);
        parameters.put("skip", skip);
        String collectionName = getCollectionName(Review.class);
        return jMingoTemplate.queryForList(collectionName + ".getByModerationStatus", Review.class, parameters);
    }


    @Override
    protected Class<Review> getDocumentType() {
        return Review.class;
    }
}
