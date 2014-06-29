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
package org.jmingo.query;

/**
 * Element with condition. Immutable.
 */
public class ConditionElement extends TextElement {

    private final String expression;

    /**
     * Constructor with parameters.
     *
     * @param text the text
     */
    public ConditionElement(String text) {
        super(text);
        this.expression = "";
    }

    /**
     * Constructor with parameters.
     *
     * @param text       the text
     * @param expression the expression
     */
    public ConditionElement(String text, String expression) {
        super(text);
        this.expression = expression;
    }

    /**
     * Gets condition expression.
     *
     * @return condition expression
     */
    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConditionElement{");
        sb.append("text='").append(getText()).append('\'');
        sb.append('}');
        sb.append("expression='").append(expression).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
