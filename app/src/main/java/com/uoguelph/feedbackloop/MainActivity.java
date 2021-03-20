package com.uoguelph.feedbackloop;

import androidx.annotation.MainThread;
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
    static private AppCompatActivity activity;

    private LineChart chart;
    private List<Entry> entry = new ArrayList<Entry>();
    private LineDataSet dataSet;
    private LineData lineData;
    private double increment;
    private Thread thread;
    private int color;

    public quickChart( LineChart chart, int c ){
        // TODO: remove increment later...
        increment = 0.05 + Math.random()/20;
        this.chart = chart;
        this.color = c;
    }

    public static void SetBaseActivity( AppCompatActivity activity ) {
        quickChart.activity = activity;
    }

    void Start() {
        // chart options
        chart.getDescription().setEnabled( false );
        chart.setTouchEnabled( true );
        chart.setDragEnabled( true );
        chart.setScaleEnabled( false );
        chart.setDrawGridBackground( false );

        // x-axis options
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled( false );
        leftAxis.setDrawGridLines( false );
        leftAxis.setDrawLabels( false );
        leftAxis.setAxisMaximum( 1.1f );
        leftAxis.setAxisMinimum( -1.1f );

        chart.getAxisRight().setEnabled( false );
        chart.getLegend().setEnabled( false );

        double initialX = Math.random() * Math.PI * 2;
        entry.add( new Entry( (float)initialX, (float)Math.sin(initialX)));

        dataSet = new LineDataSet( entry, "Values");
        dataSet.setColor(color);
        dataSet.setDrawCircles( false );
        dataSet.setAxisDependency( YAxis.AxisDependency.LEFT );
        dataSet.setDrawValues( false );

        lineData = new LineData( dataSet );
        chart.setData(lineData);

        feed();
    }

    private void AddEntry() {
        float xValue = entry.get( entry.size() - 1 ).getX();
        xValue += increment;

        // TODO: CHANGE NEXT LINE
        lineData.addEntry( new Entry( xValue, (float)Math.sin(xValue)), 0 );
        lineData.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRange( 15f, 15f );
        chart.moveViewToX(xValue);
        //chart.invalidate();
    }

    // warning this function is dangerous
    private void feed () {
        if( thread != null ) thread.interrupt();
        final Runnable runnable = () -> AddEntry();
        thread = new Thread(() -> {
            while(true) {
                activity.runOnUiThread( runnable );

                try {
                    Thread.sleep(25);
                } catch ( InterruptedException e) {
                    e.printStackTrace();
                }
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

        quickChart.SetBaseActivity( this );
        pressureChart = new quickChart( findViewById( R.id.chartPressure), Color.BLUE );
        flowChart = new quickChart( findViewById( R.id.chartFlow), Color.RED );
        volumeChart = new quickChart( findViewById( R.id.chartVolume), Color.GREEN );

        pressureChart.Start();
        flowChart.Start();
        volumeChart.Start();
    }
}