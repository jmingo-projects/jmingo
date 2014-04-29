package com.mingo.marshall;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mingo.marshall.mongo.MongoJsonToDBObjectMarshaller;
import com.mongodb.DBObject;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

public class MongoJsonToDBObjectMarshallerTest {


    @Test
    public void testMarshall() {
        JsonToDBObjectMarshaller jsonToBsonMarshaller = new MongoJsonToDBObjectMarshaller();
        //given
        Map<String, Object> parameters = ImmutableMap.<String, Object>builder()
                .put("statuses", Lists.newArrayList("not_moderated", "passed"))
                .put("created", new Date())
                .put("t", "scala").build();
        String json = "{$match : { \"moderationStatus\": { $in: \"#statuses\"}, created: { \"$gt\" : '#created'}}, tags : {$in : ['java', '#t', 'groovy']}}";
        DBObject dbObject = jsonToBsonMarshaller.marshall(json, parameters);
        System.out.println(dbObject);

        parameters = ImmutableMap.<String, Object>builder()
                .put("p", 2).build();
        json = "{$project: {moderationStatus:1, tags: 1, count: {$add: [1, '#p' ]}}}";
        dbObject = jsonToBsonMarshaller.marshall(json, parameters);
        System.out.println(dbObject);
    }

}
