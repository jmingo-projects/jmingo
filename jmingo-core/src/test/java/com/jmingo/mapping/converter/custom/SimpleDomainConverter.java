package com.jmingo.mapping.converter.custom;

import com.jmingo.mapping.convert.Converter;
import com.jmingo.domain.SimpleDomain;
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
