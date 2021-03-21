package com.uoguelph.feedbackloop;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
        entry.add( new Entry( 0f, 0f ));

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
        int numEntries = entry.size();
        // float xValue = entry.get( entry.size() - 1 ).getX();
        float xValue = (float)( numEntries * increment );

        // TODO: CHANGE NEXT LINE
        lineData.addEntry( new Entry( (float)numEntries, (float)Math.sin(xValue)), 0 );
        lineData.notifyDataChanged();
        chart.notifyDataSetChanged();
        // 500 +-= PI * 2 * 4 * 10
        chart.setVisibleXRange( 500f, 500f );
        chart.moveViewToX(numEntries);
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

        registerControlChangers(
                findViewById( R.id.textIP ),
                findViewById( R.id.buttonUp_fip ),
                findViewById( R.id.buttonDn_fip ) );

        registerControlChangers(
                findViewById( R.id.textRate ),
                findViewById( R.id.buttonUp_frate ),
                findViewById( R.id.buttonDn_frate ) );

        registerControlChangers(
                findViewById( R.id.textIE ),
                findViewById( R.id.buttonUp_fie ),
                findViewById( R.id.buttonDn_fie ) );

        registerControlChangers(
                findViewById( R.id.textPPT ),
                findViewById( R.id.buttonUp_fppt ),
                findViewById( R.id.buttonDn_fppt ) );
        registerControlChangers(
                findViewById( R.id.textPEEP ),
                findViewById( R.id.buttonUp_fpeep ),
                findViewById( R.id.buttonDn_fpeep ) );

        registerControlChangers(
                findViewById( R.id.textFO ),
                findViewById( R.id.buttonUp_ffo ),
                findViewById( R.id.buttonDn_ffo ) );



    }

    void registerControlChangers(EditText valueField, ImageButton upButton, ImageButton downButton ) {
        upButton.setOnClickListener(
                v -> {
                    int val = Integer.parseInt(String.valueOf(valueField.getText()));
                    valueField.setText( Integer.toString( val + 1 ));
                }
        );
        downButton.setOnClickListener(
                v -> {
                    int val = Integer.parseInt(String.valueOf(valueField.getText()));
                    valueField.setText( Integer.toString( val - 1 ));
                }
        );

    }

}