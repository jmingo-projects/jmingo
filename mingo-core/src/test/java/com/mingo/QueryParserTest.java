package com.mingo;

import static com.mingo.query.util.QueryUtils.createCompositeIdForQueries;
import com.mingo.query.Query;
import com.mingo.query.QuerySet;
import com.mingo.query.QueryType;
import com.mingo.exceptions.ParserException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.util.QueryUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.parser.xml.dom.QuerySetParser}.
 */
public class QueryParserTest {

    public static final String DB_TEST = "dbTest";
    public static final String COLLECTION_ONE = "collectionOne";
    public static final String QUERY_1 = "query-1";
    public static final String QUERY_2 = "query-2";

    @Test(groups = "unit")
    public void testParseQuery() throws ParserException {
        //given
        String queryXml = "/xml/query1.xml";
        Parser<QuerySet> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.QUERY);
        QuerySet querySet = xmlParser.parse(getClass().getResourceAsStream(queryXml));
        createCompositeIdForQueries(querySet);
        Assert.assertEquals(querySet.getDbName(), DB_TEST);
        Assert.assertEquals(querySet.getCollectionName(), COLLECTION_ONE);
        Assert.assertNotNull(querySet);
        Assert.assertEquals(querySet.getQueries().size(), 2);
        Query queryOne = querySet.getQueries().get(QUERY_1);
        Query queryTwo = querySet.getQueries().get(QUERY_2);
        checkQueryOne(queryOne);
        checkQueryTwo(queryTwo);
    }

    private void checkQueryOne(Query query) {
        Assert.assertEquals(query.getId(), "query-1");
        Assert.assertEquals(query.getCompositeId(), QueryUtils.buildCompositeId(DB_TEST, COLLECTION_ONE, QUERY_1));
        Assert.assertEquals(query.getQueryType(), QueryType.AGGREGATION);
        Assert.assertEquals(query.getBody(), "{'body' : 'plain-way'}");
        Assert.assertNotNull(query.getCases());
        Assert.assertTrue(CollectionUtils.isNotEmpty(query.getCases()));
    }

    private void checkQueryTwo(Query query) {
        Assert.assertEquals(query.getId(), "query-2");
        Assert.assertEquals(query.getCompositeId(), QueryUtils.buildCompositeId(DB_TEST, COLLECTION_ONE, QUERY_2));
        Assert.assertEquals(query.getQueryType(), QueryType.SIMPLE);
        Assert.assertEquals(query.getBody(), "{body' : 'query-2'}");
        Assert.assertTrue(CollectionUtils.isEmpty(query.getCases()));
    }
}
