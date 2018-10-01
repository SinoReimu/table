package cn.tecotaku.table;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "123";
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Bp bp = new Bp(3, 20, 1, 0.05);


                Random random = new Random();

                List<ArrayList<Double>> list = new ArrayList();
                for (int i = 0; i != 360; i++) {
                    ArrayList<Double> list2 = new ArrayList();

                    list2.add(Math.sin(i*Math.PI/180));
                    list2.add(Math.sin((i+1)*Math.PI/180));
                    list2.add(Math.sin((i+2)*Math.PI/180));
                    list2.add(Math.sin((i+3)*Math.PI/180));
                    list.add(list2);
                }

                for (int i = 0; i !=10; i++) {
                    System.out.println("epoch:"+i);
                    for ( ArrayList<Double>  value : list) {
                        double[] real = new double[1];
                        real[0] = value.get(3);
                        double[] binary = new double[3];
                        binary[0] = value.get(0);
                        binary[1] = value.get(1);
                        binary[2] = value.get(2);
                        bp.train(binary, real);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "训练完成,开始预测", Toast.LENGTH_SHORT).show();
                    }
                });

                for(int i=0; i<10000; i++) {

                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry((float)Math.sin((finalI+600)*Math.PI/180));
                            //bp.predict(binary,result);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        chart = findViewById(R.id.chart);

        chart.setContentDescription("");
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);



        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        chart.setData(new LineData());

        chart.invalidate();
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "25 1");
        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setValueTextColor(Color.TRANSPARENT);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }

    private void addEntry(float y) {

        LineData data = chart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry(data.getEntryCount(), y), 0);
        data.notifyDataChanged();

        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(360);
        chart.moveViewToX(data.getEntryCount() - 360);

    }


}
