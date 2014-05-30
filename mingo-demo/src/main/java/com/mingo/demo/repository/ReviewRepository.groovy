package com.mingo.demo.repository

import com.google.common.collect.Maps
import com.mingo.demo.domain.Review
import com.mingo.demo.repository.api.IBaseRepository
import com.mingo.mongo.index.Index
import org.springframework.stereotype.Repository

import javax.annotation.PostConstruct

import static com.mingo.util.DocumentUtils.getCollectionName

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
        indexes.each { getMingoTemplate().ensureIndex(Review.class, it) }
    }

    List<Review> getByAuthor(String name, String email) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("name", name);
        parameters.put("email", email);
        String collectionName = getCollectionName(Review.class);
        return mingoTemplate.queryForList(collectionName + ".getByAuthor", Review.class, parameters);
    }

    Map<String, Integer> getTagsCount(def statuses) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", statuses);
        String collectionName = getCollectionName(Review.class);
        return mingoTemplate.queryForObject(collectionName + ".getTagsCount", Map.class, parameters);
    }

    @Override
    protected Class<Review> getDocumentType() {
        return Review.class;
    }
}
