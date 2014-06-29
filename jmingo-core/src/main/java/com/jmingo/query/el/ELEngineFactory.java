/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jmingo.query.el;

/**
 * Creates EL engine instances in accordance with specified engine type.
 */
public final class ELEngineFactory {

    private ELEngineFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates EL engine for specified type.
     *
     * @param elEngineType EL engine type
     * @return implementation of {@link com.jmingo.query.el.ELEngine} interface
     */
    public static ELEngine create(ELEngineType elEngineType) throws UnsupportedOperationException {
        ELEngine queryAnalyzer;
        switch (elEngineType) {
            case JEXL:
                queryAnalyzer = new JexlELEngine();
                break;
            case SPRING_EL:
                queryAnalyzer = new SpringELEngine();
                break;
            default:
                throw new UnsupportedOperationException("unsupported EL engine type: " + elEngineType);
        }
        return queryAnalyzer;
    }
}
