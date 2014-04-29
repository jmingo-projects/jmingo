package com.mingo.query;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mongodb.util.JSONSerializers;
import com.mongodb.util.ObjectSerializer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.helpers.MessageFormatter.format;

@Deprecated
public class QueryParamPlaceHolder {

    /* this prefixed must be placed before parameter name */
    private static final String PARAMETER_PREFIX = "#";
    private static final String PARAMETER_PATTERN = "\"" + PARAMETER_PREFIX + "{}\"";
    private static final ObjectSerializer OBJECT_SERIALIZER = JSONSerializers.getLegacy();

    /**
     * Replace parameters in query.
     *
     * @param query      query
     * @param parameters parameters
     * @return prepared query
     */
    public static String replaceQueryParameters(String query, Map<String, Object> parameters) {
        if (StringUtils.isNotBlank(query) && MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                if (StringUtils.isNotEmpty(parameter.getKey())) {
                    if (isParameterEmpty(parameter.getValue())) {
                        query = StringUtils.replace(query, PARAMETER_PREFIX + parameter.getKey(), StringUtils.EMPTY);
                    } else {
                        String replacement = createReplacement(parameter.getKey(), parameter.getValue());
                        query = StringUtils.replace(query, replacement,
                                getParameterAsString(parameter.getValue()));
                    }
                }
            }
        }
        return removeParameters(query);
    }

    private static String removeParameters(String query) {
        if (StringUtils.isBlank(query)) {
            return query;
        }
        int paramPrefixIndex = query.indexOf(PARAMETER_PREFIX);
        if (paramPrefixIndex > 0) {
            return removeParameters(query.replace(getParameterPlaceholder(paramPrefixIndex, query), StringUtils.EMPTY));

        }
        return query;
    }

    private static String getParameterPlaceholder(int paramPrefixIndex, String query) {
        for (int i = paramPrefixIndex; i < query.length(); i++) {
            if ("\"".equals(String.valueOf(query.charAt(i)))) {
                return query.substring(paramPrefixIndex, i);
            }
        }
        return StringUtils.EMPTY;
    }

    private static String getParameterAsString(Object parameter) {
        if (isParameterEmpty(parameter)) {
            return StringUtils.EMPTY;
        }
        String val;
        if (isTypeSupported(parameter)) {
            if (parameter instanceof Collection) {
                val = toString((Collection) parameter);
            } else if (parameter instanceof Object[]) {
                val = toString((Object[]) parameter);
            } else if (parameter instanceof Date) {
                val = OBJECT_SERIALIZER.serialize(parameter);
            } else {
                val = parameter.toString();
            }
        } else {
            throw new RuntimeException("unsupported parameter type. parameter value: " +
                    parameter + ", class: " + parameter.getClass());
        }
        return val;
    }

    private static String toString(Object[] array) {
        return toString(Lists.newArrayList(array));
    }

    private static String toString(Collection<?> collection) {
        List<String> values = Lists.transform(Lists.newArrayList(collection), new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return "'" + input.toString() + "'";
            }
        });
        return "[" + StringUtils.join(values, ",") + "]";
    }

    private static boolean isParameterEmpty(Object val) {
        return !(isParameterNotEmpty(val));
    }

    private static boolean isParameterNotEmpty(Object val) {
        if (val == null) {
            return false;
        }
        if (val instanceof Collection) {
            return !((Collection) val).isEmpty();
        } else if (val instanceof Object[]) {
            return ((Object[]) val).length > 0;
        } else if (val instanceof String) {
            return StringUtils.isNotEmpty((String) val);
        }
        return true;
    }

    private static String createReplacement(String name, Object value) {
        return (isFullReplace(value)) ? format(PARAMETER_PATTERN, name).getMessage() : PARAMETER_PREFIX + name;
    }

    private static boolean isFullReplace(Object parameter) {
        return parameter instanceof Collection ||
                parameter instanceof Object[] ||
                parameter instanceof Number ||
                parameter instanceof Date;
    }

    private static boolean isTypeSupported(Object val) {
        return val instanceof String
                || val instanceof Number
                || val instanceof Date
                || val instanceof Collection
                || val instanceof Object[]
                || val instanceof Enum;
    }

}
