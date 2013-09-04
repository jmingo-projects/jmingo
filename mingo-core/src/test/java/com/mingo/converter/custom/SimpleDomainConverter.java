package com.mingo.converter.custom;

import com.mingo.convert.Converter;
import com.mingo.domain.SimpleDomain;
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
