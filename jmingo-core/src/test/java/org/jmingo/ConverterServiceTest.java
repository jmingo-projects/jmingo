package org.jmingo;

import org.jmingo.domain.SimpleDomain;
import org.jmingo.domain.TestDomain;
import org.jmingo.mapping.convert.Converter;
import org.jmingo.mapping.convert.ConverterService;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * unit test for {@link ConverterService}.
 */
public class ConverterServiceTest {

    @Test(groups = "unit")
    public void testConverterService() {
        ConverterService converterService = new ConverterService("org.jmingo.mapping.converter.custom, org.jmingo.mapping.converter.custom.test");
        Converter<SimpleDomain> simpleDomainConverter = converterService.lookupConverter(SimpleDomain.class);
        Converter<TestDomain> testDomainConverter = converterService.lookupConverter(TestDomain.class);
        Assert.assertNotNull(simpleDomainConverter);
        Assert.assertNotNull(testDomainConverter);
    }
}
