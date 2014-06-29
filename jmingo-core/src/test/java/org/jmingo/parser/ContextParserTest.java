package org.jmingo.parser;

import org.jmingo.config.ContextConfiguration;
import org.jmingo.exceptions.MingoParserException;
import org.jmingo.parser.Parser;
import org.jmingo.parser.xml.dom.ParserFactory;
import org.jmingo.util.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link org.jmingo.parser.xml.dom.ContextConfigurationParser}
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
        Assert.assertEquals(contextConfiguration.getDefaultConverter(), "org.jmingo.mapping.convert.DefaultConverter");
        Assert.assertEquals(contextConfiguration.getConverterPackageScan(), "org.jmingo.mapping.converter.custom");
        Assert.assertTrue(MapUtils.isNotEmpty(contextConfiguration.getMongoConfig().getOptions()));
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().size(), 1);
        Assert.assertEquals(contextConfiguration.getMongoConfig().getOptions().get("connectTimeout"), "100");
    }

}
