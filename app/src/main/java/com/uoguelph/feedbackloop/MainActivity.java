package com.uoguelph.feedbackloop;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
        xAxis.setTextColor(Color.WHITE);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled( true );
        leftAxis.setDrawGridLines( true );
        leftAxis.setDrawLabels( true );
        leftAxis.setAxisMaximum( 1.1f );
        leftAxis.setAxisMinimum( -1.1f );
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(20f);


        chart.getAxisRight().setEnabled( false );
        chart.getLegend().setEnabled( false );

        double initialX = Math.random() * Math.PI * 2;
        entry.add( new Entry( 0f, 0f ));

        dataSet = new LineDataSet( entry, "Values");
        dataSet.setColor(color);
        dataSet.setDrawCircles( false );
        dataSet.setAxisDependency( YAxis.AxisDependency.LEFT );
        dataSet.setDrawValues( false );
        dataSet.setValueTextColor(Color.WHITE);

        lineData = new LineData( dataSet );
        chart.setData(lineData);

        //BACKGROUND
        chart.setBackgroundColor(Color.BLACK);
        chart.setGridBackgroundColor(Color.BLACK);


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



public class MainActivity extends AppCompatActivity { //implements AdapterView.OnItemSelectedListener

    private quickChart pressureChart;
    private quickChart volumeChart;
    private quickChart flowChart;
    private Spinner spinner; //new
    private ImageView textPPT; //new


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quickChart.SetBaseActivity( this );
        pressureChart = new quickChart( findViewById( R.id.chartPressure), Color.CYAN );
        flowChart = new quickChart( findViewById( R.id.chartFlow), Color.RED );
        volumeChart = new quickChart( findViewById( R.id.chartVolume), Color.YELLOW );

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

       // registerControlChangers(
              //  findViewById( R.id.textPPT );
             //  findViewById( R.id.buttonUp_fppt ),
             //   findViewById( R.id.buttonDn_fppt ) );

        registerControlChangers(
                findViewById( R.id.textPEEP ),
                findViewById( R.id.buttonUp_fpeep ),
                findViewById( R.id.buttonDn_fpeep ) );

        registerControlChangers(
                findViewById( R.id.textFO ),
                findViewById( R.id.buttonUp_ffo ),
                findViewById( R.id.buttonDn_ffo ) );




        //add dropdown menu for PTT
        textPPT = findViewById(R.id.imagePPT);
        spinner = findViewById(R.id.spinner);

        String[] stringPTT = getResources().getStringArray(R.array.PTT_options);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stringPTT);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


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

/*
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ImageView textPPT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textPPT = findViewById(R.id.textPPT);
        spinner = findViewById(R.id.spinner);

        String[] stringPTT = getResources().getStringArray(R.array.PTT_options);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stringPTT);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
/*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            textPPT.setTextSize(Float.parseFloat(valueFromSpinner));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

 */

