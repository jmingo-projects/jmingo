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
import java.util.List;

import static com.mingo.util.StringUtils.appendIfAbsent;

public class IfElseConditionalConstruct implements QueryElement {

    private ConditionElement ifStatement;
    private List<ConditionElement> elseIf = Lists.newArrayList();
    private TextElement elseStatement;


    public IfElseConditionalConstruct withIf(String condition, String body) {
        this.ifStatement = new ConditionElement(body, condition);
        return this;
    }

    public IfElseConditionalConstruct elseIf(ConditionElement el) {
        elseIf.add(el);
        return this;
    }

    public IfElseConditionalConstruct elseIf(String condition, String body) {
        elseIf.add(new ConditionElement(body, condition));
        return this;
    }

    public IfElseConditionalConstruct withElse(String body) {
        this.elseStatement = new TextElement(body);
        return this;
    }

    public ConditionElement getIf() {
        return ifStatement;
    }

    public List<ConditionElement> getElseIf() {
        return ImmutableList.copyOf(elseIf);
    }

    public TextElement getElse() {
        return elseStatement;
    }

    @Override
    public void accept(QBuilder visitor) {
        if(visitor.append(ifStatement)) {
            visitor.appendIfAbsent(",");
            return;
        }
        for(ConditionElement el : elseIf) {
            if(visitor.append(el)) {
                visitor.appendIfAbsent(",");
                return;
            }
        }

        if(elseStatement != null) {
            visitor.append(elseStatement);
            visitor.appendIfAbsent(",");
        }
    }

    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder();
        append(builder, ifStatement);
        append(builder, elseIf);
        append(builder, elseStatement);
        return builder.toString();
    }

    private void append(StringBuilder builder, List<? extends QueryElement> queryElements) {
        for(QueryElement queryElement : queryElements) {
            append(builder, queryElement);
        }
    }

    private void append(StringBuilder builder, QueryElement queryElement) {
        if(queryElement != null) {
            String elText = queryElement.asString();
            builder.append(elText);
            appendIfAbsent(builder, ",");
        }
    }

    @Override
    public String toString() {
        return asString();
    }

}
