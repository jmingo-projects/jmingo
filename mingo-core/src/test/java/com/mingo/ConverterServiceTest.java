package com.mingo;

import com.mingo.mapping.convert.Converter;
import com.mingo.mapping.convert.ConverterService;
import com.mingo.domain.SimpleDomain;
import com.mingo.domain.TestDomain;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * unit test for {@link ConverterService}.
 */
public class ConverterServiceTest {

    @Test(groups = "unit")
    public void testConverterService() {
        ConverterService converterService = new ConverterService("com.mingo.mapping.converter.custom, com.mingo.mapping.converter.custom.test");
        Converter<SimpleDomain> simpleDomainConverter = converterService.lookupConverter(SimpleDomain.class);
        Converter<TestDomain> testDomainConverter = converterService.lookupConverter(TestDomain.class);
        Assert.assertNotNull(simpleDomainConverter);
        Assert.assertNotNull(testDomainConverter);
    }
}
