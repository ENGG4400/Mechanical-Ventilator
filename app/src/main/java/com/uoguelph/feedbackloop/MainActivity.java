package com.uoguelph.feedbackloop;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

class quickChart {
    private LineChart chart;
    private List<Entry> entry = new ArrayList<Entry>();
    private double increment;
    private Thread thread;
    private AppCompatActivity activity;

    public quickChart( double inc, LineChart chart, AppCompatActivity act ){
        increment = inc;
        this.chart = chart;
        activity = act;
    }

    void Start() {
        // chart options
        chart.getDescription().setEnabled( false );
        chart.setTouchEnabled( true );
        chart.setDragEnabled( true );
        chart.setScaleEnabled( false );
        chart.setDrawGridBackground( false );
        chart.setVisibleXRangeMaximum( 120 );

        // x-axis options
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines( false );
        leftAxis.setDrawLabels( false );
        leftAxis.setAxisMaximum( 1.0f );
        leftAxis.setAxisMinimum( -1.0f );

        chart.getAxisRight().setEnabled( false );
        chart.getLegend().setEnabled( false );

        double initialX = Math.random() * Math.PI * 2;
        entry.add( new Entry( (float)initialX, (float)Math.sin(initialX)));

        LineDataSet dataSet = new LineDataSet( entry, "Values");
        dataSet.setColor(Color.CYAN);

        LineData lineData = new LineData( dataSet );
        chart.setData(lineData);

        feed();
    }

    private void AddEntry() {
        float xValue = entry.get( entry.size() - 1 ).getX();
        xValue += increment;
        entry.add( new Entry( xValue, (float)Math.sin(xValue)));
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void feed () {
        if( thread != null ) thread.interrupt();
        final Runnable runnable = () -> AddEntry();
        thread = new Thread(() -> {
            while(true) {
                activity.runOnUiThread( runnable );
            }
        });
        thread.start();
    }
}


public class MainActivity extends AppCompatActivity {

    private quickChart pressureChart;
    private quickChart volumeChart;
    private quickChart flowChart;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v( "MainActivity", "onCreate >");

        pressureChart = new quickChart( 0.05, findViewById( R.id.chartPressure), this );
        flowChart = new quickChart( 0.12, findViewById( R.id.chartFlow), this );
        volumeChart = new quickChart( 0.09, findViewById( R.id.chartVolume), this );

        pressureChart.Start();
        flowChart.Start();
        volumeChart.Start();

        Log.v( "MainActivity", "onCreate <");
    }
}