package org.jmingo.mapping.converter.custom;

import org.jmingo.domain.SimpleDomain;
import org.jmingo.mapping.convert.Converter;
import com.mongodb.DBObject;

public class SimpleDomainConverter implements Converter<SimpleDomain> {

    @Override
    public SimpleDomain convert(Class<SimpleDomain> type, DBObject source) {
        return null;
    }

    public SimpleDomain customConvertMethod(DBObject source) {
        return null;
    }
}
