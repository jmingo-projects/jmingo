package com.mingo;

import com.mingo.context.Context;
import com.mingo.context.ContextLoader;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.query.Query;
import com.mingo.query.fragment.QueryFragmentInjector;
import com.mingo.util.ParameterBuilder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test to check fragments injection.
 */
public class QueryFragmentInjectorTest {

    private QueryFragmentInjector queryFragmentManager = new QueryFragmentInjector();

    @DataProvider(name = "testAggregationQueryProvider")
    public Object[][] testAggregationQueryProvider() {
        return new Object[][]{
            {new ParameterBuilder().add("name1", "").add("name2", "jeff").build(),
                "{'body' : 'query-1-fragment'},{'body2' : 'query-1-fragment'},{ \"body\" : \"query-1\"},{'body' : 'query-2-fragment'}"},
            {new ParameterBuilder().add("name1", "").add("name2", "").build(),
                "{'body' : 'query-1-fragment'},{'body2' : 'query-1-fragment'},{ \"body\" : \"query-1\"}"},
            {new ParameterBuilder().add("name1", "test").add("name2", "jeff").build(),
                "{ \"body\" : \"query-1\"},{'body' : 'query-2-fragment'}"},
            {new ParameterBuilder().add("name1", "test").add("name2", "").build(),
                "{ \"body\" : \"query-1\"}"},
        };
    }

    @Test(groups = "unit", dataProvider = "testAggregationQueryProvider")
    public void testAggregationQuery(Map<String, Object> parameters, String expected) throws ContextInitializationException {
        String contextPath = "/xml/correct_fragment/context.xml";
        // when
        Context context = ContextLoader.getInstance().load(contextPath);
        Query query = context.getQueryByCompositeId("dbTest.collection.query-1");
        String result = queryFragmentManager.injectFragments(query, parameters);
        System.out.println(result);
        Assert.assertEquals(result, expected);
        Assert.assertNotNull(query);
    }

}
