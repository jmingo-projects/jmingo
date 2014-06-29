package org.jmingo.parser;

import org.jmingo.config.ContextDefinition;
import org.jmingo.exceptions.MingoParserException;
import org.jmingo.parser.xml.dom.ParserFactory;
import org.jmingo.util.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link org.jmingo.parser.xml.dom.ContextDefinitionParser}
 */
public class ContextParserTest {

    @Test(groups = "unit")
    public void contextParserTest() throws MingoParserException {
        //given
        String contextXml = "/xml/context.xml";
        Parser<ContextDefinition> xmlParser = ParserFactory.createParser(ParserFactory.ParseComponent.CONTEXT);
        //when
        ContextDefinition contextDefinition = xmlParser.parse(FileUtils.getAbsolutePath(contextXml));
        //then
        Assert.assertTrue(CollectionUtils.isNotEmpty(contextDefinition.getQuerySetConfig().getQuerySets()));
        int countOfQuerySets = 1;
        Assert.assertEquals(contextDefinition.getQuerySetConfig().getQuerySets().size(), countOfQuerySets);
        Assert.assertTrue(contextDefinition.getQuerySetConfig().getQuerySets().contains("/xml/testQuerySet.xml"));
        Assert.assertEquals(contextDefinition.getDatabaseHost(), "localhost");
        Assert.assertEquals(contextDefinition.getDatabasePort(), 27017);
        Assert.assertEquals(contextDefinition.getMongoConfig().getDatabasePort(), 27017);
        Assert.assertEquals(contextDefinition.getMongoConfig().getDatabaseHost(), "localhost");
        Assert.assertEquals(contextDefinition.getMongoConfig().getDbName(), "dbTest");
        Assert.assertEquals(contextDefinition.getDefaultConverter(), "org.jmingo.mapping.convert.DefaultConverter");
        Assert.assertEquals(contextDefinition.getConverterPackageScan(), "org.jmingo.mapping.converter.custom");
        Assert.assertTrue(MapUtils.isNotEmpty(contextDefinition.getMongoConfig().getOptions()));
        Assert.assertEquals(contextDefinition.getMongoConfig().getOptions().size(), 1);
        Assert.assertEquals(contextDefinition.getMongoConfig().getOptions().get("connectTimeout"), "100");
    }

}
