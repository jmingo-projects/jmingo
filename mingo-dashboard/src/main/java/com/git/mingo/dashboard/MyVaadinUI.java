package com.git.mingo.dashboard;

import com.git.mingo.benchmark.client.BenchmarkClient;
import com.git.mingo.dashboard.benchmark.QueryPerformanceChart;
import com.git.mingo.dashboard.util.SpringContextHelper;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
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

import java.util.Random;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class,
        widgetset = "com.git.mingo.dashboard.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }





    Chart chart;

    final Random random = new Random();
    @Override
    protected void init(VaadinRequest request) {
        SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
        BenchmarkClient client = (BenchmarkClient) helper.getBean("benchmarkClient");
        final QueryPerformanceChart layout =  new QueryPerformanceChart(client);
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Click Me new1");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));


            }
        });


        layout.addComponent(button);
    }

}
