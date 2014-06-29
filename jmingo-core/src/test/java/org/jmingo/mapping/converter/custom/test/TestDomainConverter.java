package org.jmingo.mapping.converter.custom.test;

import org.jmingo.domain.TestDomain;
import org.jmingo.mapping.convert.Converter;
import com.mongodb.DBObject;

/**
 * Enter class description.
 * <p/>
 * Date: 8/7/13
 */
public class TestDomainConverter implements Converter<TestDomain> {
    @Override
    public TestDomain convert(Class<TestDomain> type, DBObject source) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
