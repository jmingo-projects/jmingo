package com.mingo.converter;

import static com.mingo.convert.ConversionUtils.getFirstElement;

import com.mingo.convert.Converter;
import com.mingo.domain.User;
import com.mongodb.DBObject;

public class UserConverter implements Converter<User> {

    @Override
    public User convert(Class<User> type, DBObject source) {
        source = getFirstElement(source);
        User user = new User("UserConverter::" + source.get("name"));
        user.setId((String) source.get("_id"));
        return user;
    }
}
