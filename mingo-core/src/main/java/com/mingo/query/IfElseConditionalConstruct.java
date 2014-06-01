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
package com.mingo.query;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import java.util.List;

import static com.mingo.util.StringUtils.appendIfAbsent;

/**
 * Represents if-else conditional construct. By using {@link IfElseConditionalConstruct}, it is possible to combine
 * several conditions. Only the statements following the first condition that is found to be true will be executed.
 * All other statements will be skipped.
 * <code>
 * if condition - {@link #ifStatement}
 * elseif condition - {@link #elseIf}
 * else - {@link #elseStatement}
 * </code>
 * <p/>
 * Immutable class.
 */
public class IfElseConditionalConstruct implements QueryElement {

    // required
    private final ConditionElement ifStatement;
    //optional
    private final List<ConditionElement> elseIf;
    //optional
    private final TextElement elseStatement;

    private IfElseConditionalConstruct(Builder builder) {
        this.ifStatement = builder.ifStatement;
        this.elseIf = ImmutableList.copyOf(builder.elseIf);
        this.elseStatement = builder.elseStatement;
        if (ifStatement == null) {
            throw new IllegalArgumentException("if statement is required and cannot be null");
        }
    }

    /**
     * Creates builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets IF statement.
     *
     * @return IF statement
     */
    public ConditionElement getIf() {
        return ifStatement;
    }

    /**
     * Gets else-if statements.
     *
     * @return immutable list of else-if statements
     */
    public List<ConditionElement> getElseIf() {
        return elseIf;
    }

    /**
     * Gets ELSE statement.
     *
     * @return ELSE statement
     */
    public TextElement getElse() {
        return elseStatement;
    }

    /**
     * Checks each element of current condition construct and adds in builder only one element whose expression
     * evaluates 'true' for specified parameters.
     *
     * @param qBuilder the query builder
     */
    @Override
    public void accept(QBuilder qBuilder) {
        if (qBuilder.append(ifStatement)) {
            qBuilder.appendIfAbsent(",");
            return;
        }
        for (ConditionElement el : elseIf) {
            if (qBuilder.append(el)) {
                qBuilder.appendIfAbsent(",");
                return;
            }
        }

        if (elseStatement != null) {
            qBuilder.append(elseStatement);
            qBuilder.appendIfAbsent(",");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder();
        append(builder, ifStatement);
        append(builder, elseIf);
        append(builder, elseStatement);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return asString();
    }

    private void append(StringBuilder builder, List<? extends QueryElement> queryElements) {
        for (QueryElement queryElement : queryElements) {
            append(builder, queryElement);
        }
    }

    private void append(StringBuilder builder, QueryElement queryElement) {
        if (queryElement != null) {
            String elText = queryElement.asString();
            builder.append(elText);
            appendIfAbsent(builder, ",");
        }
    }

    /**
     * Builder to create {@link IfElseConditionalConstruct}.
     */
    public static class Builder {
        private ConditionElement ifStatement;
        private List<ConditionElement> elseIf = Lists.newArrayList();
        private TextElement elseStatement;

        /**
         * Creates IF statement.
         *
         * @param condition the condition
         * @param body      the body of IF statement
         * @return this builder
         */
        public Builder withIf(String condition, String body) {
            Validate.notBlank(condition, "condition of IF statement cannot be empty");
            this.ifStatement = new ConditionElement(body, condition);
            return this;
        }

        /**
         * Adds else-if statement.
         *
         * @param el the conditional element that represents else-if statement.
         * @return this builder
         */
        public Builder elseIf(ConditionElement el) {
            Validate.notNull(el, "else-if statement must be not null to add in conditional construct");
            elseIf.add(el);
            return this;
        }

        /**
         * Adds else-if statement.
         *
         * @param condition the condition
         * @param body      the body of else-if statement
         * @return this builder
         */
        public Builder elseIf(String condition, String body) {
            Validate.notBlank(condition, "condition of ELSE-IF statement cannot be empty");
            elseIf.add(new ConditionElement(body, condition));
            return this;
        }

        /**
         * Creates Else statement.
         *
         * @param body the the body of else statement
         * @return this builder
         */
        public Builder withElse(String body) {
            this.elseStatement = new TextElement(body);
            return this;
        }

        public IfElseConditionalConstruct build() {
            return new IfElseConditionalConstruct(this);
        }

    }

}
