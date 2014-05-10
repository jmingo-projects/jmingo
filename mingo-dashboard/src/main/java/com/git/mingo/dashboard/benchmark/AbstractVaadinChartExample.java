package com.git.mingo.dashboard.benchmark;


import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class AbstractVaadinChartExample extends VerticalLayout {
    private final VerticalLayout content;

    public AbstractVaadinChartExample() {
        content = this;
        content.setSizeFull();
    }

    protected void setup() {
        Component map = getChart();
        content.addComponent(map);
        content.setExpandRatio(map, 1);
    }

    protected abstract Component getChart();

    @Override
    public void attach() {
        super.attach();
        setup();
    }


}