package com.uoguelph.feedbackloop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

class quickChart {
    private LineChart chart;
    private List<Entry> entry = new ArrayList<Entry>();
    private double increment;

    public quickChart( double inc, LineChart chart ){
        Log.v( "quickChart", "quickChart >");
        Log.v( "quickChart", "quickChart > inc: " + inc );
        Log.v( "quickChart", "quickChart > chart: " + chart );

        increment = inc;
        this.chart = chart;
        double initialX = Math.random() * Math.PI * 2;

        entry.add( new Entry( (float)initialX, (float)Math.sin(initialX)));

        Log.v( "quickChart", "entry: " + entry );

        LineDataSet dataSet = new LineDataSet( entry, "Values");
        dataSet.setColor(android.R.color.darker_gray);

        LineData lineData = new LineData( dataSet );
        chart.setData(lineData);
        chart.invalidate();

        Log.v( "quickChart", "quickChart <");
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

        pressureChart = new quickChart( 0.05, findViewById( R.id.chartPressure));
        flowChart = new quickChart( 0.12, findViewById( R.id.chartFlow));
        volumeChart = new quickChart( 0.09, findViewById( R.id.chartVolume));

        Log.v( "MainActivity", "onCreate <");
    }
}