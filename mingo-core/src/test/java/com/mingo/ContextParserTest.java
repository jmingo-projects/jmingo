package com.mingo;

import com.mingo.context.conf.ContextConfiguration;
import com.mingo.query.QueryExecutorType;
import com.mingo.exceptions.ParserException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.parser.xml.dom.ContextConfigurationParser}
 */
public class ContextParserTest {


    @Test(groups = "unit")
    public void contextParserTest() throws ParserException {
        //given
        String contextXml = "/xml/context.xml";
        Parser<ContextConfiguration> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.CONTEXT);
        //when
        ContextConfiguration contextConfiguration = xmlParser.parse(getClass().getResourceAsStream(contextXml));
        //then
        checkQuerySets(contextConfiguration);
    }

    private void checkQuerySets(ContextConfiguration contextConfiguration) {
        Assert.assertTrue(CollectionUtils.isNotEmpty(contextConfiguration.getQuerySetConfiguration().getQuerySets()));
        Assert.assertEquals(contextConfiguration.getQuerySetConfiguration().getQuerySets().size(), 2);
        Assert.assertTrue(contextConfiguration.getQuerySetConfiguration().getQuerySets().contains("/xml/query1.xml"));
        Assert.assertTrue(contextConfiguration.getQuerySetConfiguration().getQuerySets().contains("/xml/query2.xml"));
        Assert.assertEquals(QueryExecutorType.MONGO_DRIVER, contextConfiguration.getQueryExecutorType());
        Assert.assertEquals(contextConfiguration.getDatabaseHost(), "localhost");
        Assert.assertEquals(contextConfiguration.getDatabasePort(), 27017);
        Assert.assertEquals(contextConfiguration.getQuerySetConfiguration().getDatabaseName(), "dbTestCommon");
    }

}
