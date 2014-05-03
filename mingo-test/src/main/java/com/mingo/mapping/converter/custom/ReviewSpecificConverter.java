package com.mingo.mapping.converter.custom;


import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.mingo.mapping.convert.ConversionUtils.getAsInteger;
import static com.mingo.mapping.convert.ConversionUtils.getAsString;

public class ReviewSpecificConverter {

    public Map<String, Integer> convertTagsCount(DBObject source) {
        Map<String, Integer> result = newHashMap();
        if (source instanceof BasicDBList) {
            for (Object item : (BasicDBList) source) {
                DBObject dbObject = (DBObject) item;
                result.put(getAsString("_id", dbObject), getAsInteger("totalCount", dbObject));
            }
        }

        return result;
    }

}