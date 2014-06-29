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
 * Text element. Immutable.
 */
public class TextElement implements QueryElement {

    private final String text;

    /**
     * Constructor with parameters.
     *
     * @param text the element text
     */
    public TextElement(String text) {
        this.text = text;
    }

    /**
     * Gets text.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(QBuilder queryBuilder) {
        queryBuilder.append(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TextElement{");
        sb.append("text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
