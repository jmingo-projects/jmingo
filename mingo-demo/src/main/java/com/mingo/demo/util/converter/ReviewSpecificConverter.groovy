package com.mingo.demo.util.converter

import com.mingo.demo.domain.Review
import com.mingo.mapping.convert.DefaultConverter
import com.mongodb.BasicDBList
import com.mongodb.DBObject

import static com.google.common.collect.Maps.newHashMap
import static com.mingo.mapping.convert.ConversionUtils.getAsInteger
import static com.mingo.mapping.convert.ConversionUtils.getAsString


class ReviewSpecificConverter {

    public static final String ID = "_id";
    public static final String AVERAGE_RATING = "averageRating";
    public static final String COMMENTS_COUNT = "commentsCount";

    private DefaultConverter converter = new DefaultConverter();

    Map<String, Integer> convertTagsCount(DBObject source) {
        Map<String, Integer> result = newHashMap();
        if (source instanceof BasicDBList) {
            for (Object item : (BasicDBList) source) {
                DBObject dbObject = (DBObject) item;
                result.put(getAsString("_id", dbObject), getAsInteger("totalCount", dbObject));
            }
        }

        return result;
    }

    List<Review> convertVehicleReviewsWithAggregatedInfo(DBObject dbItems) {
        List<Review> reviews = new LinkedList<Review>();
        for (Object responseItem : dbItems) {
            DBObject reviewItem = (DBObject) responseItem;
            DBObject reviewIdItem = (DBObject) reviewItem.get(ID);
            Review review = convertReview(reviewIdItem);
//            if (reviewIdItem.get(AVERAGE_RATING) != null) {
//                review.setAverageRating((Double) reviewIdItem.get(AVERAGE_RATING));
//            }
            if (reviewItem.get(COMMENTS_COUNT) != null) {
                review.commentsCount = (int) reviewItem.get(COMMENTS_COUNT);
            }
            reviews.add(review);
        }
        return reviews;
    }

    protected Review convertReview(DBObject dbObject) {
        return converter.convert(Review.class, dbObject);
    }
}