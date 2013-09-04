package com.mingo;

import com.google.common.collect.ImmutableMap;
import com.mingo.context.ContextLoader;
import com.mingo.context.Context;
import com.mingo.query.QueryStatement;
import com.mingo.exceptions.ContextInitializationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.query.analyzer.QueryAnalyzer}
 */
public class QueryCaseAnalyzerTest {

    private Context context;

    @BeforeClass(groups = "unit")
    public void setUp() throws ContextInitializationException {
        context = ContextLoader.getInstance().load("/xml/correct/context.xml");
    }

    @Test(groups = "unit")
    private void testCaseOne() {
        QueryStatement queryStatement = new QueryStatement(context, "dbTest.collectionOne.query-1", ImmutableMap.<String, Object>of("name", "john"));
        Assert.assertEquals(queryStatement.getPreparedQuery(), "{'case' : 'john'},{'body' : 'query-1-fragment'}");
        Assert.assertEquals(queryStatement.getConverterClass(), "simpleDomainConverter");
        Assert.assertEquals(queryStatement.getConverterMethod(), "customConvertMethod");
    }

    @Test(groups = "unit")
    private void testTwoOne() {
        QueryStatement queryStatement = new QueryStatement(context, "dbTest.collectionOne.query-1", ImmutableMap.<String, Object>of("name", "jack"));
        Assert.assertEquals(queryStatement.getPreparedQuery(), "{'case' : 'jack'}");
        Assert.assertEquals(queryStatement.getConverterClass(), null);
        Assert.assertEquals(queryStatement.getConverterMethod(), null);
    }

    @Test(groups = "unit")
    private void testCase() {
        QueryStatement queryStatement = new QueryStatement(context, "dbTest.collectionOne.query-1", ImmutableMap.<String, Object>of("name", "noname"));
        Assert.assertEquals(queryStatement.getPreparedQuery(), "{'query' : 'body'}");
        Assert.assertEquals(queryStatement.getConverterClass(), "simpleDomainConverter");
        Assert.assertEquals(queryStatement.getConverterMethod(), "customConvertMethod");
    }

}
