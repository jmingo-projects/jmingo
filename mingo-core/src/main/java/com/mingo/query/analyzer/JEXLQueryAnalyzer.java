package com.mingo.query.analyzer;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.Validate;

import java.util.Map;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@ThreadSafe
public class JEXLQueryAnalyzer extends AbstractQueryAnalyzer implements QueryAnalyzer {

    private final JexlEngine jexl = new JexlEngine();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean evaluate(String expression, Map<String, Object> parameters) {
        Validate.notBlank(expression, "expression cannot be empty");
        Expression e = jexl.createExpression(expression);
        JexlContext jc = new MapContext();
        if (MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                jc.set(parameter.getKey(), parameter.getValue());
            }
        }
        Object result = e.evaluate(jc);
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new RuntimeException("expected boolean result of evaluation: " +
                expression + ", but the result is: " + result);
        }
    }

}

