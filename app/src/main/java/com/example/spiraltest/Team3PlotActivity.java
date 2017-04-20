package com.example.spiraltest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Team3PlotActivity extends AppCompatActivity {

    private static final String TAG = Team3PlotActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team3_plot);

        XYPlot plot = (XYPlot) findViewById(R.id.plot);
        LineAndPointFormatter format = new LineAndPointFormatter(null, Color.BLUE, null, null);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, Math.PI*2);
        plot.setUserDomainOrigin(0);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 50); // 50 is pretty arbitrary
        plot.setUserRangeOrigin(0);

        if (getIntent().getData() != null) {
            File f = new File(getIntent().getData().getPath());
            File userDrawn = new File(f.getParent(), getString(R.string.team3_user_spiral_data_name));

            try {
                boolean isCCW = checkIfCCW(f);
                Team3Trace t = new Team3Trace(0, 0, "User Spiral");
                t.readFromFile(userDrawn);
                RadiusAngleTransformer radiusAngleTransformer = new RadiusAngleTransformer(t, t.getTitle(), isCCW);
                plot.addSeries(radiusAngleTransformer, format);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
            }
        } else {
            Log.i(TAG, "No data");
        }
    }

    private boolean checkIfCCW(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("=");
            if (split.length == 2) {
                if (split[0].equals("mode")) {
                    return split[1].equals(""+Team3TracerView.TRACE_CCW);
                }
            }
        }

        return false;
    }

    private class RadiusAngleTransformer implements XYSeries {

        private double[] radiuses;
        private double[] angles;
        private String title;

        private double threshold = Math.PI;
        private double max_theta = -100;

        RadiusAngleTransformer (XYSeries original, String title, boolean isCCW) {
            this.title = title;
            radiuses = new double[original.size()];
            angles = new double[original.size()];

            int multiplier = 0;

            if (isCCW) {
                for (int i = original.size()-1; i >= 0; i -= 1) {
                    multiplier = convertPointAtIndex(original, multiplier, i);
                }
            } else {
                for (int i = 0; i < original.size(); i += 1) {
                    multiplier = convertPointAtIndex(original, multiplier, i);
                }
            }


        }

        private int convertPointAtIndex(XYSeries original, int multiplier, int index) {
            int x = original.getX(index).intValue();
            int y = original.getY(index).intValue();

            radiuses[index] = Math.sqrt(x*x + y*y);
            double theta = Math.atan2(y, x);
            angles[index] = theta + (Math.PI*2*multiplier);
            if (max_theta < angles[index]) {
                max_theta = angles[index];
            } else if (angles[index] < max_theta) {
                if (max_theta - angles[index] > threshold) {
                    multiplier += 1;
                    angles[index] += Math.PI*2;
                }
            }
            return multiplier;
        }

        @Override
        public int size() {
            return radiuses.length;
        }

        @Override
        public Number getX(int index) {
            return angles[index];
        }

        @Override
        public Number getY(int index) {
            return radiuses[index];
        }

        @Override
        public String getTitle() {
            return title;
        }
    }
}