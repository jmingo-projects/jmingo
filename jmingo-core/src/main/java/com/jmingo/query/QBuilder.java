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
package com.jmingo.query;

/**
 * Builder to build string query from multiple {@link QueryElement} objects.
 */
public interface QBuilder {

    /**
     * Appends text of the given text element to the current query.
     *
     * @param queryEl the text element to append
     * @return true if element was appended, otherwise - false
     */
    boolean append(TextElement queryEl);

    /**
     * Checks condition element and appends it if element expression
     * evaluates 'true' for specified parameters.
     *
     * @param queryEl the condition element
     * @return true if element was appended, otherwise - false
     */
    boolean append(ConditionElement queryEl);

    /**
     * Append the suffix to the query if query doesn't end with the specified suffix.
     */
    boolean appendIfAbsent(String suffix);

    /**
     * Builds query.
     *
     * @return query
     */
    String buildQuery();
}
