package com.mingo.config;

import com.mingo.context.Context;
import com.mingo.context.ContextLoader;
import com.mingo.convert.Converter;
import com.mingo.exceptions.ContextInitializationException;
import org.springframework.util.Assert;

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
public class MingoContextFactory {

    /**
     * Creates mingo context.
     *
     * @param contextPath the path to mingo context
     * @return loaded and initialized mingo context
     */
    public Context createMingoContext(String contextPath) {
        Assert.hasText(contextPath, "contextPath must not be null or empty.");
        Context context;
        try {
            context = ContextLoader.getInstance().load(contextPath);
        } catch (ContextInitializationException e) {
            throw new RuntimeException("Failed to load mingo context. context path: " + contextPath, e);
        }
        return context;
    }

    /**
     * Creates mingo context.
     *
     * @param contextPath      the path to mingo context
     * @param defaultConverter the default converter
     * @return loaded and initialized mingo context
     */
    public Context createMingoContext(String contextPath, Converter defaultConverter) {
        Assert.notNull(defaultConverter, "default converter must not be null");
        Context context = createMingoContext(contextPath);
        context.setDefaultConverter(defaultConverter);
        return context;
    }

}
