package com.mingo;

import static com.mingo.query.util.QueryUtils.buildCompositeId;
import com.mingo.context.ContextLoader;
import com.mingo.convert.DefaultConverter;
import com.mingo.context.Context;
import com.mingo.query.Query;
import com.mingo.query.QueryAnalyzerType;
import com.mingo.query.QueryCase;
import com.mingo.query.QueryExecutorType;
import com.mingo.query.QueryFragment;
import com.mingo.query.QuerySet;
import com.mingo.exceptions.ContextInitializationException;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.context.ContextLoader}
 */
public class CorrectContextLoaderTest {

    private static final String DB_TEST = "dbTest";
    private static final String DB_TEST_ROOT = "dbTestCommon";
    private static final String COLLECTION_ONE = "collectionOne";
    private static final String COLLECTION_TWO = "collectionTwo";
    private static final String QUERY_1 = "query-1";
    private static final String QUERY_2 = "query-2";
    private static final String QUERY_3 = "query-3";
    private static final String QUERY_4 = "query-4";
    private static final int DB_PORT = 27017;
    private static final String DB_HOST = "localhost";
    private static final String QUERY_NON_EXISTENT = "non-existent";

    @Test(groups = "unit")
    public void testLoadContextByRelativePath() throws ContextInitializationException {
        // given
        String contextPath = "/xml/correct/context.xml";
        // when
        Context context = ContextLoader.getInstance().load(contextPath);
        // then
        checkContext(context);
        checkQuerySets(context);
    }

    private void checkContext(Context context) {
        Assert.assertTrue(CollectionUtils.isNotEmpty(context.getQuerySets()));
        Assert.assertEquals(context.getQuerySets().size(), 2);
        Assert.assertEquals(context.getDatabasePort(), DB_PORT);
        Assert.assertEquals(context.getDatabaseHost(), DB_HOST);
        Assert.assertEquals(context.getQueryExecutorType(), QueryExecutorType.MONGO_DRIVER);
        Assert.assertEquals(context.getQueryAnalyzerType(), QueryAnalyzerType.JEXL);
//        Assert.assertNotNull(context.getConverters());
//        Assert.assertNotNull(context.getConverters().get("simpleDomainConverter"));
//        Assert.assertTrue(context.getConverters().get("simpleDomainConverter") instanceof SimpleDomainConverter);
        Assert.assertNotNull(context.getDefaultConverter());
        Assert.assertTrue(context.getDefaultConverter() instanceof DefaultConverter);
    }

    private void checkQuerySets(Context context) {
        QuerySet querySetOne = context.getQuerySetByPath("/xml/correct/query1.xml");
        QuerySet querySetTwo = context.getQuerySetByPath("/xml/correct/query2.xml");
        checkQuerySetOne(querySetOne);
        checkQuerySetTwo(querySetTwo);
        Query queryOne = getQueryByCompositeId(context, buildCompositeId(DB_TEST, COLLECTION_ONE, QUERY_1));
        Query queryTwo = getQueryByCompositeId(context, buildCompositeId(DB_TEST, COLLECTION_ONE, QUERY_2));
        Query queryThree = getQueryByCompositeId(context, buildCompositeId(DB_TEST_ROOT, COLLECTION_TWO, QUERY_3));
        Query queryFour = getQueryByCompositeId(context, buildCompositeId(DB_TEST_ROOT, COLLECTION_TWO, QUERY_4));
        Query notExist = getQueryByCompositeId(context, buildCompositeId(DB_TEST, COLLECTION_ONE, QUERY_NON_EXISTENT));
        Assert.assertNotNull(queryOne);
        Assert.assertNotNull(queryTwo);
        Assert.assertNotNull(queryThree);
        Assert.assertNotNull(queryFour);
        Assert.assertNull(notExist);
        checkQueryOne(queryOne);
        checkQueryTwo(queryTwo);
        checkQueryThree(queryThree);
        checkQueryFour(queryFour);
    }

    private void checkQuerySetOne(QuerySet querySet) {
        Assert.assertNotNull(querySet, "'/xml/correct/query1.xml' mot found");
        Assert.assertEquals(querySet.getQueries().size(), 2);
        Assert.assertEquals(querySet.getDbName(), "dbTest");
        Assert.assertEquals(querySet.getCollectionName(), "collectionOne");
        Query queryOne = querySet.getQueries().get("query-1");
        Query queryTwo = querySet.getQueries().get("query-2");
        checkQueryOne(queryOne);
        checkQueryTwo(queryTwo);
    }

    private void checkQuerySetTwo(QuerySet querySet) {
        Assert.assertNotNull(querySet, "'/xml/correct/query2.xml' mot found");
        Assert.assertEquals(querySet.getQueries().size(), 2);
        Assert.assertEquals(querySet.getDbName(), "dbTestCommon");
        Query queryThree = querySet.getQueries().get("query-3");
        Query queryFour = querySet.getQueries().get("query-4");
        Assert.assertNotNull(querySet.getQueryFragments());
        Assert.assertTrue(CollectionUtils.isNotEmpty(querySet.getQueryFragments()));
        QueryFragment queryFragment = querySet.getQueryFragments().iterator().next();
        Assert.assertEquals(queryFragment.getId(), "testF");
        Assert.assertEquals(queryFragment.getBody(), "{'body' : 'query-4-fragment'}");
        checkQueryThree(queryThree);
        checkQueryFour(queryFour);
    }

    private Query getQueryByCompositeId(Context context, String id) {
        return context.getQueryByCompositeId(id);
    }

    private void checkQueryOne(Query query) {
        Assert.assertEquals(query.getId(), "query-1");
        Assert.assertEquals(query.getBody(), "{'query' : 'body'}");
        Assert.assertEquals(query.getConverter(), "simpleDomainConverter");
        Assert.assertNotNull(query.getCases());
        Assert.assertTrue(CollectionUtils.isNotEmpty(query.getCases()));
        Assert.assertEquals(query.getCases().size(), 3);
        QueryCase queryCase1 = query.getQueryCaseById("caseOne");
        QueryCase queryCase2 = query.getQueryCaseById("caseTwo");
        QueryCase queryCase3 = query.getQueryCaseById("caseThree");
        Assert.assertEquals(queryCase1.getId(), "caseOne");
        Assert.assertEquals(queryCase1.getCondition(), "name == 'john'");
        Assert.assertEquals(queryCase1.getBody(), "{'case' : '#name'},{'body' : 'query-1-fragment'}");
        Assert.assertEquals(queryCase1.getConverter(), "simpleDomainConverter");
        Assert.assertEquals(queryCase1.getConverterMethod(), "customConvertMethod");
        Assert.assertEquals(queryCase1.getPriority(), 10);

        Assert.assertEquals(queryCase2.getId(), "caseTwo");
        Assert.assertEquals(queryCase2.getCondition(), "name == 'jack'");
        Assert.assertEquals(queryCase2.getBody(), "{'case' : '#name'}");
        Assert.assertEquals(queryCase2.getConverter(), null);
        Assert.assertEquals(queryCase2.getConverterMethod(), null);
        Assert.assertEquals(queryCase2.getPriority(), 5);

        Assert.assertEquals(queryCase3.getId(), "caseThree");
        Assert.assertEquals(queryCase3.getCondition(), "name == 'jack'");
        Assert.assertEquals(queryCase3.getBody(), "{'case' : '#name'}");
        Assert.assertEquals(queryCase3.getConverter(), "simpleDomainConverter");
        Assert.assertEquals(queryCase3.getConverterMethod(), "customConvertMethod");
        Assert.assertEquals(queryCase3.getPriority(), 0);
    }

    private void checkQueryTwo(Query query) {
        Assert.assertEquals(query.getId(), "query-2");
        Assert.assertEquals(query.getBody(), "{'body' : 'query-2'}");
        Assert.assertTrue(CollectionUtils.isEmpty(query.getCases()));
    }

    private void checkQueryThree(Query query) {
        Assert.assertEquals(query.getId(), "query-3");
        Assert.assertEquals(query.getBody(), "{'body' : 'query-3'}");
        Assert.assertTrue(CollectionUtils.isEmpty(query.getCases()));
    }

    private void checkQueryFour(Query query) {
        Assert.assertEquals(query.getId(), "query-4");
        Assert.assertEquals(query.getBody(), "{'body' : 'query-4'},{'body' : 'query-4-fragment'},{'body' : 'query-4-continue'}");
        Assert.assertTrue(CollectionUtils.isEmpty(query.getCases()));
    }

}
