package com.jmingo.mapping.marshall;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jmingo.domain.TestEnum;
import com.jmingo.mapping.marshall.jackson.MongoMapper;
import com.jmingo.mapping.marshall.mongo.MongoJsonToDBObjectMarshaller;
import com.mongodb.DBObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

public class MongoJsonToDBObjectMarshallerTest {
    private JsonToDBObjectMarshaller jsonToBsonMarshaller = new MongoJsonToDBObjectMarshaller();

    private ObjectMapper objectMapper = new MongoMapper();

    @DataProvider(name = "testReplaceQueryParametersDataSource")
    public Object[][] testReplaceQueryParametersDataSource() throws JsonProcessingException {
        return new Object[][]{
                // Collection
                {
                        "{$match : { \"moderationStatus\": { $in: \"#statuses\"}, \"created\": { \"$gt\" : \"#created\"}}}",
                        createParameters("statuses", Lists.newArrayList("not_moderated")),
                        "{$match : { \"moderationStatus\": { $in: ['not_moderated']}, \"created\": { \"$gt\" : \"\"}}}"
                },

                // Array
                {
                        "{$match : { \"moderationStatus\": { $in: \"#statuses\"}, \"created\": { \"$gt\" : \"#created\"}}}",
                        createParameters("statuses", Lists.newArrayList("not_moderated").toArray()),
                        "{$match : { \"moderationStatus\": { $in: ['not_moderated']}, \"created\": { \"$gt\" : \"\"}}}"
                },

                // Date
                {
                        "{$match : { \"moderationStatus\": { $in: \"#statuses\"}, \"created\": { \"$gt\" : \"#created\"}}}",
                        createParameters("created", new Date(1376850651343L)),
                        "{$match : { \"moderationStatus\": { $in: \"\"}, \"created\": { \"$gt\" : "
                                + objectMapper.writeValueAsString(new Date(1376850651343L)) + "}}}"
                },

                // Number
                {
                        "{$match : { \"rating\": \"#rating\"}}",
                        createParameters("rating", new Integer(5)),
                        "{$match : { \"rating\": 5}}"
                },

                // Enum
                {
                        "{$match : { \"field1\": \"#fieldParam\"}}",
                        createParameters("fieldParam", TestEnum.ONE),
                        "{$match : { \"field1\": \"ONE\"}}"
                },

                // Null
                {
                        "{$match : { \"field1\": \"#fieldParam\"}}",
                        createParameters(null, TestEnum.ONE),
                        "{$match : { \"field1\": \"\"}}"
                },

                // Empty
                {
                        "{$match : { \"field1\": \"#fieldParam\"}}",
                        createParameters("", TestEnum.ONE),
                        "{$match : { \"field1\": \"\"}}"
                }

        };
    }

    @Test
    public void testMarshall() {

        //given
        Date dateParam = new Date();
        Map<String, Object> parameters = ImmutableMap.<String, Object>builder()
                .put("statuses", Lists.newArrayList("not_moderated", "passed"))
                .put("created", dateParam.getTime())
                .put("t", "scala").build();
        String json = "{$match : { \"moderationStatus\": { $in: \"#statuses\"}, created: { \"$gt\" : '#created'}}, tags : {$in : ['java', '#t', 'groovy']}}";
        DBObject dbObject = jsonToBsonMarshaller.marshall(json, parameters);
        System.out.println(dbObject);
        assertEquals("{ \"$match\" : { \"moderationStatus\" : { \"$in\" : [ \"not_moderated\" , \"passed\"]} , \"created\" : { \"$gt\" : " + dateParam.getTime() + "}} , \"tags\" : { \"$in\" : [ \"java\" , \"scala\" , \"groovy\"]}}", dbObject.toString());

        parameters = ImmutableMap.<String, Object>builder()
                .put("p", 2).build();
        json = "{$project: {moderationStatus:1, tags: 1, count: {$add: [1, '#p' ]}}}";
        dbObject = jsonToBsonMarshaller.marshall(json, parameters);
        System.out.println(dbObject);
        assertEquals("{ \"$project\" : { \"moderationStatus\" : 1 , \"tags\" : 1 , \"count\" : { \"$add\" : [ 1 , 2]}}}", dbObject.toString());
    }

    // todo choose behavior for absent parameters
    //@Test(dataProvider = "testReplaceQueryParametersDataSource")
    public void testReplaceQueryParameters(String query, Map<String, Object> parameters, String expectedResult) {
        Assert.assertEquals(jsonToBsonMarshaller.marshall(query, parameters), expectedResult);
    }

    private Map<String, Object> createParameters(String paramName, Object paramValue) {
        Map<String, Object> parameter = Maps.newHashMap();
        parameter.put(paramName, paramValue);
        return parameter;
    }


}
