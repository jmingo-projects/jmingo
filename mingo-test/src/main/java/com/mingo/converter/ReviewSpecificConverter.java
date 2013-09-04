package com.mingo.converter;

import static com.mingo.convert.ConversionUtils.getAsInteger;
import static com.mingo.convert.ConversionUtils.getAsString;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.util.Map;


public class ReviewSpecificConverter {

    public Map<String, Integer> convertCountByTags(DBObject source) {
        Map<String, Integer> result = Maps.newHashMap();
        if (source instanceof BasicDBList) {
            for (Object item : (BasicDBList) source) {
                DBObject dbObject = (DBObject) item;
                result.put(getAsString("_id", dbObject), getAsInteger("totalCount", dbObject));
            }
        }

        return result;
    }
}
