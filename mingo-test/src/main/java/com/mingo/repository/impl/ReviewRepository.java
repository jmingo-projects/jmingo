package com.mingo.repository.impl;


import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.api.IBaseRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class ReviewRepository extends AbstractRepository<Review> implements IBaseRepository<String, Review> {

    @Override
    protected Class<Review> getDocumentType() {
        return Review.class;
    }


    public List<Review> findByModerationStatuses(Set<ModerationStatus> statuses) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", Iterables.transform(statuses, Enum::name));
        return mingoTemplate.queryForList(Review.COLLECTION_NAME + ".getByStatuses", Review.class, parameters);
    }

    public Map<String, Integer> getTagsCount(ModerationStatus moderationStatus) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", Arrays.asList(moderationStatus.name()));
        return mingoTemplate.queryForObject(Review.COLLECTION_NAME + ".getTagsCount", Map.class, parameters);
    }

    public Map<String, Integer> getTagsCount() {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", Collections.emptyList());
        return mingoTemplate.queryForObject(Review.COLLECTION_NAME + ".getTagsCount", Map.class);
    }
}
