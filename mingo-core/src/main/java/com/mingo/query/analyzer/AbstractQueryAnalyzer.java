package com.mingo.query.analyzer;

import com.mingo.query.Query;
import com.mingo.query.QueryCase;
import org.apache.commons.collections.CollectionUtils;
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
public abstract class AbstractQueryAnalyzer implements QueryAnalyzer {

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryCase analyzeAndGet(Query query, Map<String, Object> parameters) {
        QueryCase queryCase = null;
        Validate.notNull(query, "query cannot be null.");
        if (CollectionUtils.isNotEmpty(query.getCases())) {
            for (QueryCase qCase : query.getCases()) {
                if (evaluate(qCase.getCondition(), parameters)) {
                    queryCase = qCase;
                    break;
                }
            }
        }
        return queryCase;
    }

}
