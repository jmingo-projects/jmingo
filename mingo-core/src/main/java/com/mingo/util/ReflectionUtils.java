package com.mingo.util;


import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {

    public static List<Field> getFields(Class<?> type) {
        return Lists.newArrayList(type.getDeclaredFields());
    }

    public static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = getFields(type);
        fields.addAll(getInheritedFields(type));
        return fields;
    }

}
