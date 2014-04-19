package com.mingo.parser;

import com.mingo.config.ContextConfiguration;
import com.mingo.exceptions.MingoParserException;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.QueryExecutorType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.parser.xml.dom.ContextConfigurationParser}
 */
public class ContextParserTest {

    @Test(groups = "unit")
    public void contextParserTest() throws MingoParserException {
        //given
        String contextXml = "/xml/correct/context.xml";
        Parser<ContextConfiguration> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.CONTEXT);
        //when
        ContextConfiguration contextConfiguration = xmlParser.parse(getClass().getResourceAsStream(contextXml));
        //then
        Assert.assertTrue(CollectionUtils.isNotEmpty(contextConfiguration.getQuerySetConfiguration().getQuerySets()));
        Assert.assertEquals(contextConfiguration.getQuerySetConfiguration().getQuerySets().size(), 2);
        Assert.assertTrue(contextConfiguration.getQuerySetConfiguration().getQuerySets().contains("/xml/correct/query1.xml"));
        Assert.assertTrue(contextConfiguration.getQuerySetConfiguration().getQuerySets().contains("/xml/correct/query2.xml"));
        Assert.assertEquals(QueryExecutorType.MONGO_DRIVER, contextConfiguration.getQueryExecutorType());
        Assert.assertEquals(contextConfiguration.getDatabaseHost(), "localhost");
        Assert.assertEquals(contextConfiguration.getDatabasePort(), 27017);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDatabasePort(), 27017);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDatabaseHost(), "localhost");
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDbName(), "dbTest");
        Assert.assertEquals(contextConfiguration.getDefaultConverter(), "com.mingo.convert.DefaultConverter");
        Assert.assertEquals(contextConfiguration.getConverterPackageScan(), "com.mingo.converter.custom");
        Assert.assertTrue(MapUtils.isNotEmpty(contextConfiguration.getMongoConfig().getOptions()));
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().size(), 1);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().get("connectTimeout"), "100");
    }

}
