package com.pnf.penequillaunch.mainActivity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyticsFragment extends Fragment {


    View v;

    public AnalyticsFragment() {
        // Required empty public constructor
    }

    private View findViewById(@IdRes int id) {
        return v.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_analytics, container, false);
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> dataPointLineGraphSeries = new LineGraphSeries<>(
                new DataPoint[]{
                        new DataPoint(0, 1),
                        new DataPoint(1, 4),
                        new DataPoint(2, 3),
                        new DataPoint(3, 5)
                });
        dataPointLineGraphSeries.setColor(Color.parseColor("#E53935"));
        graphView.addSeries(dataPointLineGraphSeries);
        GraphView graphView2 = (GraphView) findViewById(R.id.graph2);
        LineGraphSeries<DataPoint> dataPointLineGraphSeries2 = new LineGraphSeries<>(
                new DataPoint[]{
                        new DataPoint(0, 1),
                        new DataPoint(1, 2),
                        new DataPoint(2, -3),
                        new DataPoint(3, 1)
                });
        dataPointLineGraphSeries2.setColor(Color.parseColor("#00BCD4"));
        graphView2.addSeries(dataPointLineGraphSeries2);
        graphView.addSeries(dataPointLineGraphSeries2);
        GraphView graphView3 = (GraphView) findViewById(R.id.graph3);
        LineGraphSeries<DataPoint> dataPointLineGraphSeries3 = new LineGraphSeries<>(
                new DataPoint[]{
                        new DataPoint(0, 1),
                        new DataPoint(1, 6),
                        new DataPoint(2, 7),
                        new DataPoint(3, 8)
                });
        dataPointLineGraphSeries3.setColor(Color.parseColor("#FFC107"));
        graphView3.addSeries(dataPointLineGraphSeries3);
        graphView.addSeries(dataPointLineGraphSeries3);
        graphView.getViewport().setScalable(true);
        graphView2.getViewport().setScalable(true);
        graphView3.getViewport().setScalable(true);

        graphView.getViewport().setScalableY(true);
        graphView2.getViewport().setScalableY(true);
        graphView3.getViewport().setScalableY(true);

        graphView.getViewport().setScrollable(true);
        graphView2.getViewport().setScrollable(true);
        graphView3.getViewport().setScrollable(true);

        graphView.getViewport().setScrollableY(true);
        graphView2.getViewport().setScrollable(true);
        graphView3.getViewport().setScrollable(true);
        return v;
    }

}
