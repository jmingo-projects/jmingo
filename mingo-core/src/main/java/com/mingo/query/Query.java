package com.mingo.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mingo.query.el.ELEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.mingo.util.QueryUtils.pipeline;

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
public class Query {

    private final String id;

    private String compositeId;

    private String converter;

    private String converterMethod;

    /* escape null parameters*/
    private boolean escapeNullParameters = true;

    private QueryType queryType = QueryType.PLAIN;

    private List<QueryElement> queryElements = Lists.newArrayList();

    public Query() {
        id = "";
    }

    /**
     * Constructor with parameters.
     *
     * @param id id
     */
    public Query(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();
        queryElements.forEach(el -> {
            String elText = el.asString();
            builder.append(elText);
            if (!elText.endsWith(",")) {
                builder.append(",");
            }
        });
        String text = replaceLast(builder.toString(), ",", "");
        return QueryType.PLAIN.equals(queryType) ? text : pipeline(text);
    }

    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    /**
     * Gets composite ID.
     *
     * @return composite ID
     */
    public String getCompositeId() {
        return compositeId;
    }

    /**
     * Set composite id.
     *
     * @param compositeId composite id
     */
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * Gets converter.
     *
     * @return converter
     */
    public String getConverter() {
        return converter;
    }

    /**
     * Sets converter.
     *
     * @param converter converter
     */
    public void setConverter(String converter) {
        if (StringUtils.isNotBlank(converter)) {
            this.converter = converter;
        }

    }

    /**
     * Gets converter method.
     *
     * @return converter method
     */
    public String getConverterMethod() {
        return converterMethod;
    }

    /**
     * Sets converter method.
     *
     * @param converterMethod converter method
     */
    public void setConverterMethod(String converterMethod) {
        if (StringUtils.isNotBlank(converterMethod)) {
            this.converterMethod = converterMethod;
        }
    }

    /**
     * Gets query type.
     *
     * @return query type
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * Sets queryType.
     *
     * @param queryType query type
     */
    public void setQueryType(QueryType queryType) {
        if (queryType != null) {
            this.queryType = queryType;
        }
    }


    /**
     * Gets escape null parameters mode.
     *
     * @return true or false
     */
    public boolean isEscapeNullParameters() {
        return escapeNullParameters;
    }

    /**
     * Sets escape null parameters mode.
     *
     * @param escapeNullParameters true or false
     */
    public void setEscapeNullParameters(boolean escapeNullParameters) {
        this.escapeNullParameters = escapeNullParameters;
    }

    public Query add(QueryElement queryEl) {
        queryElements.add(queryEl);
        return this;
    }

    public Query add(List<QueryElement> qElements) {
        queryElements.addAll(qElements);
        return this;
    }

    public Query addTextElement(String text) {
        queryElements.add(new TextElement(text));
        return this;
    }

    public List<QueryElement> getQueryElements() {
        return ImmutableList.copyOf(queryElements);
    }

    public String build(ELEngine elEngine, Map<String, Object> parameters) {
        QBuilder queryBuilder = new QueryBuilder(elEngine, parameters);
        queryElements.forEach(element -> element.accept(queryBuilder));
        return queryBuilder.buildQuery();
    }

}
