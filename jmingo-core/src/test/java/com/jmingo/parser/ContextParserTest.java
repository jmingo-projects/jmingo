package com.jmingo.parser;

import com.jmingo.config.ContextConfiguration;
import com.jmingo.exceptions.MingoParserException;
import com.jmingo.parser.xml.dom.ParserFactory;
import com.jmingo.util.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link com.jmingo.parser.xml.dom.ContextConfigurationParser}
 */
public class ContextParserTest {

    @Test(groups = "unit")
    public void contextParserTest() throws MingoParserException {
        //given
        String contextXml = "/xml/context.xml";
        Parser<ContextConfiguration> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.CONTEXT);
        //when
        ContextConfiguration contextConfiguration = xmlParser.parse(FileUtils.getAbsolutePath(contextXml));
        //then
        Assert.assertTrue(CollectionUtils.isNotEmpty(contextConfiguration.getQuerySetConfiguration().getQuerySets()));
        int countOfQuerySets = 1;
        Assert.assertEquals(contextConfiguration.getQuerySetConfiguration().getQuerySets().size(), countOfQuerySets);
        Assert.assertTrue(contextConfiguration.getQuerySetConfiguration().getQuerySets().contains("/xml/testQuerySet.xml"));
        Assert.assertEquals(contextConfiguration.getDatabaseHost(), "localhost");
        Assert.assertEquals(contextConfiguration.getDatabasePort(), 27017);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDatabasePort(), 27017);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDatabaseHost(), "localhost");
        Assert.assertEquals(contextConfiguration.getMongoConfig().getDbName(), "dbTest");
        Assert.assertEquals(contextConfiguration.getDefaultConverter(), "com.jmingo.mapping.convert.DefaultConverter");
        Assert.assertEquals(contextConfiguration.getConverterPackageScan(), "com.jmingo.mapping.converter.custom");
        Assert.assertTrue(MapUtils.isNotEmpty(contextConfiguration.getMongoConfig().getOptions()));
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().size(), 1);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().get("connectTimeout"), "100");
    }

}
