package com.mingo;

import com.mingo.context.Context;
import com.mingo.context.ContextLoader;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.query.Query;
import com.mingo.query.fragment.QueryFragmentInjector;
import com.mingo.util.ParameterBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test to check fragments injection.
 */
public class QueryFragmentInjectorTest {

    private QueryFragmentInjector queryFragmentManager = new QueryFragmentInjector();

    private Context context;

    @BeforeClass
    private void setUp() throws ContextInitializationException {
        context = ContextLoader.getInstance().load("/xml/correct_fragment/context.xml");
    }

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
    public void testAggregationQueryOne(Map<String, Object> parameters, String expected) throws ContextInitializationException {
        Query query = context.getQueryByCompositeId("dbTest.collection.query-1");
        String result = queryFragmentManager.injectFragments(query, parameters);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void testAggregationQueryTwo() throws ContextInitializationException {
        Query query = context.getQueryByCompositeId("dbTest.collection.query-2");
        String result1 = queryFragmentManager.injectFragments(query, new ParameterBuilder().add("name1", "test").build());
        Assert.assertEquals(result1, "{'body' : 'query-1-fragment'},{'body2' : 'query-1-fragment'}");

        String result2 = queryFragmentManager.injectFragments(query, new ParameterBuilder().add("name1", "").build());
        Assert.assertEquals(result2, "");
    }

}
