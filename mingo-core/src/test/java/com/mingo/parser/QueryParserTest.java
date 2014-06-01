package com.mingo.parser;

import com.mingo.exceptions.MingoParserException;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.Query;
import com.mingo.query.QuerySet;
import com.mingo.query.QueryType;
import com.mingo.util.QueryUtils;
import com.mingo.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;



/**
 * Test for {@link com.mingo.parser.xml.dom.QuerySetParser}.
 */
public class QueryParserTest {

    public static final String COLLECTION_ONE = "collectionOne";
    public static final String QUERY_1 = "query-1";
    public static final String QUERY_2 = "query-2";


    @Test(groups = "unit")
    public void testParseQuery() throws MingoParserException {
        //given
        String queryXml = "/xml/testQuerySet.xml";
        Parser<QuerySet> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.QUERY);
        QuerySet querySet = xmlParser.parse(FileUtils.getAbsolutePath(queryXml));
        Assert.assertEquals(querySet.getCollectionName(), COLLECTION_ONE);
        Assert.assertNotNull(querySet);
        Assert.assertEquals(querySet.getQueries().size(), 2);
        Query queryOne = querySet.getQueryMap().get(QUERY_1);
        checkQueryOne(queryOne);
    }

    private void checkQueryOne(Query query) {
        Assert.assertEquals(query.getId(), "query-1");
        Assert.assertEquals(query.getCompositeId(), QueryUtils.buildCompositeId(COLLECTION_ONE, QUERY_1));
        Assert.assertEquals(query.getQueryType(), QueryType.AGGREGATION);
        String expected = "[{'query-1_body' : 'start root text'},{'ifClause': 'name is john'},{'elseIfClause': 'name is not john'},{'elseClause': 'name is undefined'},{'query-1_body' : 'end root text'}]";
        Assert.assertEquals(query.getText(), expected);
        Assert.assertEquals(query.getConverterClass(), "simpleDomainConverter");
        Assert.assertEquals(query.getConverterMethod(), "customConvertMethod");
        Assert.assertEquals(query.getQueryElements().size(), 3);
    }
}
