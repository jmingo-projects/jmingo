package com.mingo.query.nq;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class IfElseConditionalConstruct implements QueryElement {

    private ConditionElement ifStatement;
    private List<ConditionElement> elseIf = Lists.newArrayList();
    private TextElement elseStatement;


    public IfElseConditionalConstruct withIf(String condition, String body) {
        this.ifStatement = new ConditionElement(body, condition);
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
        if (visitor.append(ifStatement)) {
            return;
        }
        for (ConditionElement el : elseIf) {
            if (visitor.append(el)) {
                return;
            }
        }

        if (elseStatement != null) {
            visitor.append(elseStatement);
        }
    }
}
