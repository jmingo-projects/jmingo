package com.mingo.query.el;

/**
 * Copyright 2012-2013 The Mingo Team
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
public final class ELEngineFactory {

    private ELEngineFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates query analyzer for specified type.
     *
     * @param queryAnalyzerType query analyzer type
     * @return implementation of {@link com.mingo.query.el.ELEngine} interface
     */
    public static ELEngine create(ELEngineType queryAnalyzerType) {
        ELEngine queryAnalyzer;
        switch (queryAnalyzerType) {
            case JEXL:
                queryAnalyzer = new JexlELEngine();
                break;
            case SPRING_EL:
                queryAnalyzer = new SpringELEngine();
                break;
            default:
                // todo throw an exception instead
                queryAnalyzer = new JexlELEngine();
        }
        return queryAnalyzer;
    }
}
