package com.mingo.converter.custom.test;

import com.mingo.convert.Converter;
import com.mingo.domain.TestDomain;
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
