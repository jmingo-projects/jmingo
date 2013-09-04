package com.mingo.query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
public class QueryFragment {

    private String id;

    private String body;

    /**
     * Default constructor.
     */
    public QueryFragment() {
    }

    /**
     * Constructor with parameters.
     *
     * @param id   id
     * @param body body
     */
    public QueryFragment(String id, String body) {
        this.id = id;
        this.body = body;
    }

    /**
     * Gets id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets body.
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets body.
     *
     * @param body body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryFragment)) {
            return false;
        }

        QueryFragment that = (QueryFragment) o;
        return new EqualsBuilder()
            .append(id, that.id)
            .append(body, that.body)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(body)
            .toHashCode();
    }
}
