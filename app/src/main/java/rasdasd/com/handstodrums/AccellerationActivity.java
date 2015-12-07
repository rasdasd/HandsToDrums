package rasdasd.com.handstodrums;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;

public class AccellerationActivity extends Activity {
    private TextView result;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float x, y, z;
    private LineChart chart;
    private float[] xarr, yarr, zarr;
    private float[] xarrfft, yarrfft, zarrfft;
    private ArrayList<Entry> chartdataX, chartdataY, chartdataZ;
    private ArrayList<String> xVals, xVals2;
    private LineDataSet dataX, dataY, dataZ;
    private int counter = -1;
    private int fftcounter = -1;
    private int maxSizeGraph = 100;
    private int fftsize = 20;
    private BarChart fftchart;
    private FloatFFT_1D fft;
    private int a = 0;
    private float threshold = 30;
    private long threshdelay = 300;
    private boolean drawDisplay = false;
    private ToggleButton bassB, floorB, mountB, snareB, resetB;
    private TextView bassT, floorT, mountT, snareT;
    private int bassC, floorC, mountC, snareC;
    private boolean learned = false;
    private int currentClass = -1;
    private int classes = 4;
    private Graph graph;
    MLP_Holder MLPHolder;
    ML1_Holder ML1Holder;
    ML2_Holder ML2Holder;
    ML3_Holder ML3Holder;
    ML4_Holder ML4Holder;
    SoundManager sm;
    private Button holderButton;
    private String[] holderStrings = {"MLP","ML1","ML2","ML3","ML4"};
    private int holderCurrent;
    private int holderCount = 5;
    private Holder[] holderArray = new Holder[holderCount];
    private TextView[][] textViews;
    private int[][] correct,wrong;
    private NumberPicker np;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        graph = new Graph(this, fftsize);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        xarr = new float[fftsize];
        yarr = new float[fftsize];
        zarr = new float[fftsize];
        xarrfft = new float[fftsize * 2];
        yarrfft = new float[fftsize * 2];
        zarrfft = new float[fftsize * 2];
        result = (TextView) findViewById(R.id.result);
        result.setText("No result yet");
        bassB = (ToggleButton) findViewById(R.id.buttonBass);
        mountB = (ToggleButton) findViewById(R.id.buttonMounted);
        floorB = (ToggleButton) findViewById(R.id.buttonFloor);
        snareB = (ToggleButton) findViewById(R.id.buttonSnare);
        resetB = (ToggleButton) findViewById(R.id.reset);
        View.OnClickListener buttonlisten = new View.OnClickListener() {
            public void onClick(View v) {
                ToggleButton vb = (ToggleButton) v;
                if (vb.isChecked()) {
                    if (!bassB.equals(vb))
                        bassB.setChecked(false);
                    else {
                        currentClass = 0;
                        sm.playSound(currentClass);
                    }
                    if (!mountB.equals(vb))
                        mountB.setChecked(false);
                    else {
                        currentClass = 1;
                        sm.playSound(currentClass);
                    }
                    if (!floorB.equals(vb))
                        floorB.setChecked(false);
                    else {
                        currentClass = 2;
                        sm.playSound(currentClass);
                    }
                    if (!snareB.equals(vb))
                        snareB.setChecked(false);
                    else {
                        currentClass = 3;
                        sm.playSound(currentClass);
                    }
                } else
                    currentClass = -1;
            }
        };
        bassB.setOnClickListener(buttonlisten);
        mountB.setOnClickListener(buttonlisten);
        floorB.setOnClickListener(buttonlisten);
        snareB.setOnClickListener(buttonlisten);
        bassT = (TextView) findViewById(R.id.bassCount);
        mountT = (TextView) findViewById(R.id.mntCount);
        floorT = (TextView) findViewById(R.id.flrCount);
        snareT = (TextView) findViewById(R.id.snareCount);
        bassC = floorC = mountC = snareC = 0;
        MLPHolder = new MLP_Holder(classes, fftsize * 9);
        ML1Holder = new ML1_Holder(classes, fftsize * 9);
        ML2Holder = new ML2_Holder(classes, fftsize * 9);
        ML3Holder = new ML3_Holder(classes, fftsize * 9);
        holderArray[0] = MLPHolder;
        holderArray[1] = ML1Holder;
        holderArray[2] = ML2Holder;
        holderArray[3] = ML3Holder;
        Holder[] ml4HolderArr = new Holder[4];
        //ml4 array, votes
        ml4HolderArr[0] = MLPHolder;
        ml4HolderArr[1] = ML1Holder;
        ml4HolderArr[2] = ML2Holder;
        ml4HolderArr[3] = ML3Holder;
        ML4Holder = new ML4_Holder(ml4HolderArr,classes);
        holderArray[4] = ML4Holder;
        LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.bottomHorLayout);
        textViews = new TextView[holderArray.length][classes];
        correct=new int[holderArray.length][classes];
        wrong=new int[holderArray.length][classes];
        for(int i = 0; i < holderArray.length; i++)
        {
            LinearLayout vertLay = new LinearLayout(this);
            vertLay.setOrientation(LinearLayout.VERTICAL);
            vertLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView title = new TextView(this);
            TextView bassTV = new TextView(this);
            TextView mountTV = new TextView(this);
            TextView floorTV = new TextView(this);
            TextView snareTV = new TextView(this);
            title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            bassTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mountTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            floorTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            snareTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            title.setText("ML"+(i));
            bassTV.setText("B: ");
            mountTV.setText("M: ");
            floorTV.setText("F: ");
            snareTV.setText("S: ");
            vertLay.addView(title);
            vertLay.addView(bassTV);
            vertLay.addView(mountTV);
            vertLay.addView(floorTV);
            vertLay.addView(snareTV);
            bottomLayout.addView(vertLay);
            textViews[i][0] = bassTV;
            textViews[i][1] = mountTV;
            textViews[i][2] = floorTV;
            textViews[i][3] = snareTV;
            for(int j = 0; j < classes; j++)
            {
                correct[i][j] = 0;
                wrong[i][j] = 0;
            }
        }
        holderButton = (Button) findViewById(R.id.buttonML);
        holderCurrent = 0;
        sm = new SoundManager(this);
        fft = new FloatFFT_1D(fftsize);
        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                threshold = newVal*10;
                System.out.println(threshold);
            }
        });
    }

    public void MLButtonClick(View v)
    {
        holderCurrent = (holderCurrent + 1) % holderCount;
        ((Button)v).setText(holderStrings[holderCurrent]);
    }

    private void manipData() {
        fftcounter = (fftcounter + 1) % fftsize;
        xarr[fftcounter] = x;
        yarr[fftcounter] = y;
        zarr[fftcounter] = z;
        xarrfft = new float[fftsize * 2];
        yarrfft = new float[fftsize * 2];
        zarrfft = new float[fftsize * 2];
        for (int i = 1; i <= fftsize; i++) {
            xarrfft[fftsize - i] = xarr[(fftcounter + i) % fftsize];
            yarrfft[fftsize - i] = yarr[(fftcounter + i) % fftsize];
            zarrfft[fftsize - i] = zarr[(fftcounter + i) % fftsize];
        }
        fft.complexForward(xarrfft);
        fft.complexForward(yarrfft);
        fft.complexForward(zarrfft);
        if (System.currentTimeMillis() - lastplayed < threshdelay) {
            return;
        }
        lastplayed = System.currentTimeMillis();
        if (!learned)
            addpoint(xarr, yarr, zarr, xarrfft, yarrfft, zarrfft);
        else
            classifyPoint(xarr, yarr, zarr, xarrfft, yarrfft, zarrfft);
    }

    private void refreshDisplay() {
        graph.refreshDisplay(x, y, z, xarrfft, yarrfft, zarrfft);
    }

    public void trainingTime(View v) {
        if (!learned) {
            bassB.setChecked(false);
            mountB.setChecked(false);
            floorB.setChecked(false);
            snareB.setChecked(false);
            for (Holder h : holderArray) {
                h.train();
                System.out.println(h.getClass().toString()+" finished");
            }
            System.out.println("DONE TRAINING");
            LinearLayout layLearn = (LinearLayout)findViewById(R.id.layLearn);
            layLearn.setVisibility(LinearLayout.GONE);
            learned = true;
            bassC = 0;
            mountC = 0;
            floorC = 0;
            snareC = 0;
            for(int i = 0; i < holderArray.length; i++) {
                for (int j = 0; j < classes; j++) {
                    correct[i][j] = 0;
                    wrong[i][j] = 0;
                }
            }
        }
        ((ToggleButton) v).setChecked(false);
    }
    long lastplayed = 0;
    private void classifyPoint(float[] xarr, float[] yarr, float[] zarr, float[] xarrfft, float[] yarrfft, float[] zarrfft) {
        float[] datapoint = new float[fftsize * 9];
        float abssum = 0;
        for (int i = 0; i < fftsize * 2; i++) {
            datapoint[i] = xarrfft[i];
            abssum += Math.abs(datapoint[i]);
            datapoint[i + fftsize * 2] = yarrfft[i];
            abssum += Math.abs(datapoint[i + fftsize * 2]);
            datapoint[i + 2 * fftsize * 2] = zarrfft[i];
            abssum += Math.abs(datapoint[i + 2 * fftsize * 2]);
        }
        int startpoint = fftsize*6;
        for (int i = 0; i < fftsize; i++) {
            datapoint[startpoint + i] = xarr[i];
            datapoint[startpoint + fftsize + i] = yarr[i];
            datapoint[startpoint + fftsize * 2 + i] = zarr[i];
        }
        if (abssum > threshold) {
            Holder holder = holderArray[holderCurrent];
            for(int i = 0; i < holderArray.length; i++) {
                Holder h = holderArray[i];
                int cata = h.classify(datapoint);
                if(holderCurrent==i)
                    sound(cata);
                if(currentClass>=0)
                {
                    if(currentClass==cata)
                        incrementCorrect(i,currentClass);
                    else
                        decrement(i,currentClass);
                }
            }
        }
    }

    private void addpoint(float[] xarr, float[] yarr, float[] zarr, float[] xarrfft, float[] yarrfft, float[] zarrfft) {
        float[] datapoint = new float[fftsize * 9];
        float abssum = 0;
        for (int i = 0; i < fftsize * 2; i++) {
            datapoint[i] = xarrfft[i];
            abssum += Math.abs(datapoint[i]);
            datapoint[i + fftsize * 2] = yarrfft[i];
            abssum += Math.abs(datapoint[i + fftsize * 2]);
            datapoint[i + 2 * fftsize * 2] = zarrfft[i];
            abssum += Math.abs(datapoint[i + 2 * fftsize * 2]);
        }
        int startpoint = fftsize*6;
        for (int i = 0; i < fftsize; i++) {
            datapoint[startpoint + i] = xarr[i];
            datapoint[startpoint + fftsize + i] = yarr[i];
            datapoint[startpoint + fftsize * 2 + i] = zarr[i];
        }
        if (currentClass >= 0) {
            if (abssum > threshold) {
                for(Holder h : holderArray) {
                    h.addDataPoint(datapoint, currentClass);
                }
                increment(currentClass);
            }
        }
    }

    private void increment(int thisclass) {
        switch (thisclass) {
            case 0:
                bassC++;
                bassT.setText("B: " + bassC);
                break;
            case 1:
                mountC++;
                mountT.setText("M: " + mountC);
                break;
            case 2:
                floorC++;
                floorT.setText("F: " + floorC);
                break;
            case 3:
                snareC++;
                snareT.setText("S: " + snareC);
                break;
        }
    }

    private void incrementCorrect(int alg, int thisclass) {
        correct[alg][thisclass]++;
        switch (thisclass) {
            case 0:
                textViews[alg][thisclass].setText("B: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 1:
                textViews[alg][thisclass].setText("M: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 2:
                textViews[alg][thisclass].setText("F: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 3:
                textViews[alg][thisclass].setText("S: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
        }
    }
    private void decrement(int alg, int thisclass) {
        wrong[alg][thisclass]--;
        switch (thisclass) {
            case 0:
                textViews[alg][thisclass].setText("Bass: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 1:
                textViews[alg][thisclass].setText("Mount: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 2:
                textViews[alg][thisclass].setText("Floor: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
            case 3:
                textViews[alg][thisclass].setText("Snare: " + correct[alg][thisclass] + " " + wrong[alg][thisclass]);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, sensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(accelerationListener);
        super.onStop();
    }

    private final static float alpha = 0.8f;
    float gravity[] = new float[3];
    private SensorEventListener accelerationListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            x = event.values[0] - gravity[0];
            y = event.values[1] - gravity[1];
            z = event.values[2] - gravity[2];
            result.setText(String.format("x is: %f / y is: %f / z is: %f", x, y, z));
            manipData();
            refreshDisplay();
        }
    };

    private void sound(int drumclass) {
        sm.playSound(drumclass);
    }

    public void reset(View v) {
        currentClass = -1;
        learned = false;
        resetB.setChecked(false);
        bassB.setChecked(false);
        mountB.setChecked(false);
        floorB.setChecked(false);
        snareB.setChecked(false);
        MLPHolder = new MLP_Holder(classes, fftsize * 9);
        ML1Holder = new ML1_Holder(classes, fftsize * 9);
        ML2Holder = new ML2_Holder(classes, fftsize * 9);
        ML3Holder = new ML3_Holder(classes, fftsize * 9);
        holderArray[0] = MLPHolder;
        holderArray[1] = ML1Holder;
        holderArray[2] = ML2Holder;
        holderArray[3] = ML3Holder;
        Holder[] ml4HolderArr = new Holder[4];
        ml4HolderArr[0] = MLPHolder;
        ml4HolderArr[1] = ML1Holder;
        ml4HolderArr[2] = ML2Holder;
        ml4HolderArr[3] = ML3Holder;
        ML4Holder = new ML4_Holder(ml4HolderArr,classes);
        holderArray[4] = ML4Holder;
        bassC = 0;
        mountC = 0;
        floorC = 0;
        snareC = 0;
        LinearLayout layLearn = (LinearLayout)findViewById(R.id.layLearn);
        layLearn.setVisibility(LinearLayout.VISIBLE);
        for(int i = 0; i<holderArray.length; i++)
        {
            for(int j = 0; j<classes; j++)
            {
                textViews[i][j].setText("");
                correct[i][j] = 0;
                wrong[i][j] = 0;
            }
        }
        bassT.setText("Bass: ");
        mountT.setText("Mount: ");
        floorT.setText("Floor: ");
        snareT.setText("Snare: ");
    }
}