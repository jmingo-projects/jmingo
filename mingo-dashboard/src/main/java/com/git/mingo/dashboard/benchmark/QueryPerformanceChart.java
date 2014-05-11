package com.git.mingo.dashboard.benchmark;

import com.git.mingo.benchmark.client.BenchmarkClient;
import com.google.common.base.Throwables;
import com.mingo.benchmark.Metrics;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Axis;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dmgcodevil on 08.05.2014.
 */
public class QueryPerformanceChart extends AbstractVaadinChartExample {

    public Chart _chart;
    BenchmarkClient client;

    public QueryPerformanceChart(BenchmarkClient client) {
        this.client = client;
    }

    @Override
    protected Component getChart() {
        final Random random = new Random();

        final Chart chart = new Chart();
        chart.setWidth("500px");

        final Configuration configuration = new Configuration();
        configuration.getChart().setType(ChartType.LINE);
        configuration.getTitle().setText("Query execution time benchmark");

        Axis xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setTickPixelInterval(150);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new Title("Average execution time (ms)"));
        yAxis.setMin(0);


        //yAxis.setPlotLines(new PlotLine(0, 1, new SolidColor("#808080")));

        configuration.getTooltip().setEnabled(true);
       //configuration.getLegend().setEnabled(false);

        configuration
                .getTooltip()
                .setFormatter(
                        "''+ this.series.name +''+ ': '+ this.y +' ms'");

        List<DataSeries> dataSeriesList = new ArrayList<>();
        client.getQueriesNames().forEach(queryName -> {
            final DataSeries dataSeries = new DataSeries(queryName);
            dataSeries.setPlotOptions(new PlotOptionsSpline());
            dataSeries.setName(queryName);
            dataSeriesList.add(dataSeries);
        });

        dataSeriesList.forEach((dataSeries) -> {

            for (int i = -19; i <= 0; i++) {
                dataSeries.add(new DataSeriesItem(
                        System.currentTimeMillis() + i * 1000, random.nextDouble()));
            }
            runWhileAttached(chart, new Runnable() {
                long prev = 0;
                @Override
                public void run() {

                    List<Metrics> metricsList = client.getMetrics(dataSeries.getName());

                    if (CollectionUtils.isNotEmpty(metricsList)) {
                        for (Metrics metrics : metricsList) {
                            //long y = TimeUnit.MILLISECONDS.convert(avgTime, TimeUnit.NANOSECONDS);
                            long y = metrics.getExecutionTime() / 10000;
                            //long x = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(metrics.getStartTime(), TimeUnit.NANOSECONDS);
                            long x = DateUtils.addSeconds(new Date(), 1).getTime();
                            prev = y;
                            dataSeries.add(new DataSeriesItem(x, y), true, true);
                        }

                    } else {
                        long x = DateUtils.addSeconds(new Date(), 1).getTime();
                        dataSeries.add(new DataSeriesItem(x, prev), true, true);
                    }
                }
            }, 1000, 1000);
        });

        dataSeriesList.forEach((dataSeries) -> {
            configuration.addSeries(dataSeries);
        });

        chart.drawChart(configuration);
        _chart = chart;
        return chart;
    }

    private double calculateAverage(List<Metrics> metricsList) {
        long sum = 0;
        if (CollectionUtils.isNotEmpty(metricsList)) {
            for (Metrics metrics : metricsList) {
                sum += metrics.getExecutionTime();
            }
            return sum / metricsList.size();
        }
        return sum;
    }

    public static void runWhileAttached(final Component component,
                                        final Runnable task, final int interval, final int initialPause) {
        // Until reliable push available in our demo servers
        UI.getCurrent().setPollInterval(interval);

        final Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(initialPause);
                    while (true) {
                        Future<Void> future = component.getUI().access(task);
                        future.get();
                        Thread.sleep(interval);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Throwables.propagate(e);
                }
                System.out.println("Thread stopped");
            }
        };
        thread.start();
    }

}
