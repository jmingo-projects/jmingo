package com.mingo.parser;

import com.beust.jcommander.internal.Maps;
import com.mingo.exceptions.MingoParserException;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.Query;
import com.mingo.query.QueryCase;
import com.mingo.query.QuerySet;
import com.mingo.query.QueryType;
import com.mingo.query.util.QueryUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static com.mingo.query.util.QueryUtils.createCompositeIdForQueries;

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
        String queryXml = "/xml/correct/query1.xml";
        Parser<QuerySet> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.QUERY);
        QuerySet querySet = xmlParser.parse(getClass().getResourceAsStream(queryXml));
        createCompositeIdForQueries(querySet);
        Assert.assertEquals(querySet.getCollectionName(), COLLECTION_ONE);
        Assert.assertNotNull(querySet);
        Assert.assertEquals(querySet.getQueries().size(), 2);
        Query queryOne = querySet.getQueryMap().get(QUERY_1);
        Query queryTwo = querySet.getQueryMap().get(QUERY_2);
        checkQueryOne(queryOne);
        checkQueryTwo(queryTwo);
    }

    private void checkQueryOne(Query query) {
        Assert.assertEquals(query.getId(), "query-1");
        Assert.assertEquals(query.getCompositeId(), QueryUtils.buildCompositeId(COLLECTION_ONE, QUERY_1));
        Assert.assertEquals(query.getQueryType(), QueryType.AGGREGATION);
        Assert.assertEquals(query.getBody(), "{'query' : 'body'}");
        Assert.assertEquals(query.getConverter(), "simpleDomainConverter");
        Assert.assertEquals(query.getConverterMethod(), "customConvertMethod");
        checkQueryOneCases(query);
    }

    private void checkQueryTwo(Query query) {
        Assert.assertEquals(query.getId(), "query-2");
        Assert.assertEquals(query.getCompositeId(), QueryUtils.buildCompositeId(COLLECTION_ONE, QUERY_2));
        Assert.assertEquals(query.getQueryType(), QueryType.SIMPLE);
        Assert.assertEquals(query.getBody(), "{'body' : 'query-2'}");
        Assert.assertTrue(CollectionUtils.isEmpty(query.getCases()));
    }

    private void checkQueryOneCases(Query query) {
        Assert.assertTrue(CollectionUtils.isNotEmpty(query.getCases()));
        Assert.assertEquals(query.getCases().size(), 3);
        Map<String, QueryCase> caseMap = Maps.newHashMap();
        query.getCases().forEach(queryCase -> {
            caseMap.put(queryCase.getId(), queryCase);
        });
        Assert.assertEquals(caseMap.get("caseOne").getCondition(), "name == 'john'");
        Assert.assertEquals(caseMap.get("caseOne").getPriority(), 10);
        Assert.assertEquals(caseMap.get("caseOne").getBody(), "{'case' : '#name'},{'body' : 'query-1-fragment'}");
        Assert.assertEquals(caseMap.get("caseOne").getConverter(), "simpleDomainConverter");
        Assert.assertEquals(caseMap.get("caseOne").getConverterMethod(), "customConvertMethod");

        Assert.assertEquals(caseMap.get("caseTwo").getCondition(), "name == 'jack'");
        Assert.assertEquals(caseMap.get("caseTwo").getPriority(), 5);
        Assert.assertEquals(caseMap.get("caseTwo").getBody(), "{'case' : '#name'}");
        Assert.assertEquals(caseMap.get("caseTwo").getConverter(), null);
        Assert.assertEquals(caseMap.get("caseTwo").getConverterMethod(), null);

        Assert.assertEquals(caseMap.get("caseThree").getCondition(), "name == 'jack'");
        Assert.assertEquals(caseMap.get("caseThree").getPriority(), 0);
        Assert.assertEquals(caseMap.get("caseThree").getBody(), "{'case' : '#name'}");
        Assert.assertEquals(caseMap.get("caseThree").getConverter(), "simpleDomainConverter");
        Assert.assertEquals(caseMap.get("caseThree").getConverterMethod(), "customConvertMethod");
    }
}
