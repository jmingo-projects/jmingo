package com.mingo;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mingo.domain.TestEnum;
import com.mingo.query.util.QueryUtils;
import com.mongodb.util.JSONSerializers;
import com.mongodb.util.ObjectSerializer;
import java.util.Date;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link  QueryUtils}
 */
public class QueryUtilsTest {

    private static final ObjectSerializer OBJECT_SERIALIZER = JSONSerializers.getLegacy();

    @DataProvider(name = "badCompositeIds")
    public Object[][] badCompositeIds() {
        return new Object[][]{
            {null},
            {""},
            {"dbName.collectionName"},
            {" .collectionName.query"},
            {"dbName. .query"},
            {"dbName.collectionName. "},
            {" . . "}
        };
    }

    @DataProvider(name = "testReplaceQueryParametersDataSource")
    public Object[][] testReplaceQueryParametersDataSource() {
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
                    + OBJECT_SERIALIZER.serialize(new Date(1376850651343L)) + "}}}"
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


    @Test(dataProvider = "badCompositeIds",
        expectedExceptions = {NullPointerException.class, IllegalArgumentException.class})
    public void testValidateBadCompositeId(String compositeId) {
        QueryUtils.validateCompositeId(compositeId);
    }

    @Test(dataProvider = "testReplaceQueryParametersDataSource")
    public void testReplaceQueryParameters(String query, Map<String, Object> parameters, String expectedResult) {
        Assert.assertEquals(QueryUtils.replaceQueryParameters(query, parameters), expectedResult);
    }

    private Map<String, Object> createParameters(String paramName, Object paramValue) {
        Map<String, Object> parameter = Maps.newHashMap();
        parameter.put(paramName, paramValue);
        return parameter;
    }

}
