package org.jmingo.parser;

import org.jmingo.exceptions.JMingoParserException;
import org.jmingo.parser.xml.dom.ParserFactory;
import org.jmingo.query.Query;
import org.jmingo.query.QuerySet;
import org.jmingo.query.QueryType;
import org.jmingo.util.QueryUtils;
import org.jmingo.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;



/**
 * Test for {@link org.jmingo.parser.xml.dom.QuerySetParser}.
 */
public class QueryParserTest {

    public static final String COLLECTION_ONE = "collectionOne";
    public static final String QUERY_1 = "query-1";
    public static final String QUERY_2 = "query-2";


    @Test(groups = "unit")
    public void testParseQuery() throws JMingoParserException {
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
