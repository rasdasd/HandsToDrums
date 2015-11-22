package rasdasd.com.handstodrums;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by David on 11/22/2015.
 */

public class Graph {
    private SensorManager sensorManager;
    private Sensor sensor;
    private LineChart chart;
    private ArrayList<Entry> chartdataX, chartdataY, chartdataZ;
    private ArrayList<String> xVals, xVals2;
    private LineDataSet dataX, dataY, dataZ;
    private int counter = -1;
    private int fftcounter = -1;
    private int maxSizeGraph = 100;
    private BarChart fftchart;
    private int fftsize;
    public Graph(AccellerationActivity context, int init_fftsize)
    {
        chartdataX = new ArrayList<Entry>();
        chartdataY = new ArrayList<Entry>();
        chartdataZ = new ArrayList<Entry>();
        xVals = new ArrayList<String>();
        xVals2 = new ArrayList<String>();
        fftsize = init_fftsize;
        for (int i = 0; i < maxSizeGraph; i++) {
            xVals.add(i + "");
        }
        for (int i = 0; i < fftsize*2; i++) {
            if(i%2==0)
                xVals2.add(i/2 + "");
            else
                xVals2.add(i/2 + "I");
        }
        dataX = new LineDataSet(new LinkedList<Entry>(chartdataX), "X Vals");
        dataY = new LineDataSet(new LinkedList<Entry>(chartdataY), "Y Vals");
        dataZ = new LineDataSet(new LinkedList<Entry>(chartdataZ), "Z Vals");
        chart = (LineChart) context.findViewById(R.id.chart);
        chart.getAxisLeft().setAxisMaxValue(10f);
        chart.getAxisLeft().setAxisMinValue(-10f);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(false);
        fftchart = (BarChart) context.findViewById(R.id.chartfft);
        fftchart.getAxisLeft().setAxisMaxValue(10f);
        fftchart.getAxisLeft().setAxisMinValue(-10f);
        fftchart.getAxisRight().setEnabled(false);
        fftchart.setDrawValueAboveBar(false);
        fftchart.setDrawHighlightArrow(false);
        fftchart.setTouchEnabled(false);
    }
    public void refreshDisplay(float x, float y, float z, float[] xarrfft, float[] yarrfft, float[] zarrfft)
    {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        for (int i = 0; i < fftsize*2; i++) {
            yVals1.add(new BarEntry(xarrfft[i], i));
            yVals2.add(new BarEntry(yarrfft[i], i));
            yVals3.add(new BarEntry(zarrfft[i], i));
        }
        BarDataSet set1 = new BarDataSet(yVals1, "X");
        set1.setColor(Color.RED);
        BarDataSet set2 = new BarDataSet(yVals2, "Y");
        set2.setColor(Color.GREEN);
        BarDataSet set3 = new BarDataSet(yVals3, "Z");
        set3.setColor(Color.BLUE);
        ArrayList<BarDataSet> fftdataSets = new ArrayList<BarDataSet>();
        fftdataSets.add(set1);
        fftdataSets.add(set2);
        fftdataSets.add(set3);
        counter = (counter + 1) % maxSizeGraph;
        if (chartdataX.size() >= maxSizeGraph) {
            chartdataX.set(counter, new Entry(x, counter));
            chartdataY.set(counter, new Entry(y, counter));
            chartdataZ.set(counter, new Entry(z, counter));
        } else {
            chartdataX.add(new Entry(x, counter));
            chartdataY.add(new Entry(y, counter));
            chartdataZ.add(new Entry(z, counter));
        }
        dataX = new LineDataSet(chartdataX, "X");
        dataY = new LineDataSet(chartdataY, "Y");
        dataZ = new LineDataSet(chartdataZ, "Z");
        dataX.setColor(Color.RED);
        dataY.setColor(Color.GREEN);
        dataZ.setColor(Color.BLUE);
        dataX.setDrawCircles(false);
        dataY.setDrawCircles(false);
        dataZ.setDrawCircles(false);
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(dataX);
        dataSets.add(dataY);
        dataSets.add(dataZ);
        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
        BarData datafft = new BarData(xVals2, fftdataSets);
        fftchart.setData(datafft);
        fftchart.invalidate();
    }


}
