package com.git.mingo.dashboard;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class,
        widgetset = "com.git.mingo.dashboard.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
        Chart chart = new Chart(ChartType.BAR);
        chart.setWidth("400px");
        chart.setHeight("300px");

// Modify the default configuration a bit
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Planets");
        conf.setSubTitle("The bigger they are the harder they pull");
        conf.getLegend().setEnabled(false); // Disable legend

// The data
        ListSeries series = new ListSeries("Diameter");
        series.setData(4900, 12100, 12800,
            6800, 143000, 125000,
            51100, 49500);
        conf.addSeries(series);

// Set the category labels on the axis correspondingly
        XAxis xaxis = new XAxis();
        xaxis.setCategories("Mercury", "Venus", "Earth",
            "Mars", "Jupiter", "Saturn",
            "Uranus", "Neptune");
        xaxis.setTitle("Planet");
        conf.addxAxis(xaxis);

// Set the Y axis title
        YAxis yaxis = new YAxis();
        yaxis.setTitle("Diameter");
        yaxis.getLabels().setFormatter(
            "function() {return Math.floor(this.value/1000) + \'Mm\';}");
        yaxis.getLabels().setStep(2);
        conf.addyAxis(yaxis);

        layout.addComponent(chart);

        layout.addComponent(button);
    }

}
