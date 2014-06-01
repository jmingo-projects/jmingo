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
package com.mingo.query;

/**
 * Represent query fragment. used to embed common query parts in other queries.
 */
public class QueryFragment {

    private final String id;
    private final String body;

    /**
     * Default constructor.
     */
    public QueryFragment() {
        this.id = "";
        this.body = "";
    }

    /**
     * Constructor with parameters.
     *
     * @param id   the fragment id
     * @param body the fragment body
     */
    public QueryFragment(String id, String body) {
        this.id = id;
        this.body = body;
    }

    /**
     * Gets fragment id.
     *
     * @return fragment id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets query body.
     *
     * @return query body
     */
    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QueryFragment{");
        sb.append("id='").append(id).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
