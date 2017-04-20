package com.example.spiraltest;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Team12 extends AppCompatActivity {
    ImageView spiralView;
    Display display;
    Bitmap bmp;
    Bitmap alteredBitmap;
    Bitmap saveBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    Path path;
    PathMeasure measure;
    int[] colors;
    int breaks;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    float approxLength = 278.03f;
    boolean noTouchesYet = true;
    long startTime;
    long endTime;
    ArrayList<Double> spiralControlPoints;
    ArrayList<Double> runningMinControlPointDistances;
    int[] spiralTopLeft = new int[2];
    double widthScale;
    double heightScale;
    Paint distTestPaint;
    CheckBox ctrlPtsCheckBox;
    // setting this to true will mess up the length scoring metric,
    // so only use this for debugging
    static boolean METRIC_DEBUGGING = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team12);
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        spiralView = (ImageView) findViewById(R.id.spiral);
        spiralView.setImageResource(R.drawable.team12spiral);
        //spiralView.setAdjustViewBounds(true);
        // wait until layout is done to get the relative position info of the spiralView
        findViewById(R.id.activity_team12).getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT > 16) {
                            findViewById(R.id.activity_team12).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        else {
                            findViewById(R.id.activity_team12).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        spiralView.getLocationOnScreen(spiralTopLeft);
                        // Calculate scaling factors between this image and the original image, so that
                        // control points in the original image can be reconciled with the drawn image
                        // first, we calculate the width/height of the resource in pixels
                        BitmapFactory.Options dims = new BitmapFactory.Options();
                        dims.inJustDecodeBounds = true;
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.team12spiral, dims);
//                Log.i("hi", String.format("%d %d", dims.outWidth, dims.outHeight));
                        // next, we get the scaling factor by dividing the displayed dimensions
                        // by the original dimensions
                        widthScale = spiralView.getWidth() / (double) dims.outWidth;
                        heightScale = spiralView.getHeight() / (double) dims.outHeight;
                    }
                });

        distTestPaint = new Paint();
        distTestPaint.setStrokeWidth(10);
        distTestPaint.setColor(Color.CYAN);
        ctrlPtsCheckBox = (CheckBox) findViewById(R.id.ctrlPtsCheckBox);
        spiralControlPoints = new ArrayList<>();
        runningMinControlPointDistances = new ArrayList<>();

        addListenerButton();
        addListenerButtonDone();
        //addListenerButtonHistory();
    }

    private void addControlPoints() {
        ArrayList<Double> relativeSpiralControlPoints = new ArrayList<>();
        // outermost "start" of spiral
        relativeSpiralControlPoints.add(200.0);
        relativeSpiralControlPoints.add(30.0);
        // the "3 o'clock" position
        relativeSpiralControlPoints.add(304.0);
        relativeSpiralControlPoints.add(172.0);
        // the "6 o'clock" position
        relativeSpiralControlPoints.add(163.0);
        relativeSpiralControlPoints.add(313.0);
        // the "9 o'clock" position
        relativeSpiralControlPoints.add(37.0);
        relativeSpiralControlPoints.add(186.0);
        // the "12 o'clock" position
        relativeSpiralControlPoints.add(152.0);
        relativeSpiralControlPoints.add(74.0);
        // inner 3 o'clock
        relativeSpiralControlPoints.add(251.0);
        relativeSpiralControlPoints.add(171.0);
        // inner 6 o'clock
        relativeSpiralControlPoints.add(166.0);
        relativeSpiralControlPoints.add(262.0);
        // inner 9 o'clock
        relativeSpiralControlPoints.add(89.0);
        relativeSpiralControlPoints.add(193.0);
        // inner 12 o'clock
        relativeSpiralControlPoints.add(150.0);
        relativeSpiralControlPoints.add(125.0);
        // double-inner 3 o'clock
        relativeSpiralControlPoints.add(200.0);
        relativeSpiralControlPoints.add(172.0);
        // double-inner 6 o'clock
        relativeSpiralControlPoints.add(162.0);
        relativeSpiralControlPoints.add(209.0);
        // double-inner 9 o'clock
        relativeSpiralControlPoints.add(141.0);
        relativeSpiralControlPoints.add(187.0);
        // double-inner 12 o'clock
        relativeSpiralControlPoints.add(150.0);
        relativeSpiralControlPoints.add(174.0);
        // innermost "end" of the spiral
        relativeSpiralControlPoints.add(158.0);
        relativeSpiralControlPoints.add(178.0);

        double[] nonRelativePoint;
        double rx, ry;
        for (int j = 0; j < (relativeSpiralControlPoints.size() / 2); j++) {
            rx = relativeSpiralControlPoints.get(2 * j);
            ry = relativeSpiralControlPoints.get((2 * j) + 1);
            nonRelativePoint = convertPointFromRelative(rx, ry);
            Log.i("hi", String.format("Converting point (%.3f, %.3f) to (%.3f, %.3f)", rx, ry, nonRelativePoint[0], nonRelativePoint[1]));
            spiralControlPoints.add(nonRelativePoint[0]);
            spiralControlPoints.add(nonRelativePoint[1]);
        }
        for (int i = 0; i < (spiralControlPoints.size() / 2); i++) {
            runningMinControlPointDistances.add(Double.POSITIVE_INFINITY);
        }
    }

    private void addListenerButton(){
        Button btnTrace = (Button) findViewById(R.id.traceStart);
        btnTrace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addTouchListener();

                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.team12spiral);
                alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getHeight(), bmp.getConfig());

                setNewImage(alteredBitmap, bmp);
                METRIC_DEBUGGING = ctrlPtsCheckBox.isChecked();
                ctrlPtsCheckBox.setEnabled(false);
            }
        });
    }

    private void addListenerButtonDone(){
        final Button btnDone = (Button) findViewById(R.id.traceDone);
        final Button btnStart = (Button) findViewById(R.id.traceStart);
        final TextView directions = (TextView) findViewById(R.id.directions);
        ImageView spiral = (ImageView) findViewById(R.id.spiral);

        btnDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime = System.currentTimeMillis();
                // Get the duration, in seconds
                final double duration = (endTime - startTime) / 1000.0;
//                Log.i("hi", String.format("Just got duration %.3f ", duration));

                Log.d("Button:", "Finished");
                measure = new PathMeasure(path, false);
                DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                final float measuredLength = measure.getLength()/TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
                spiralView.setOnTouchListener(null);
                float distErrorSum = 0;
                for (Double m : runningMinControlPointDistances) {
                    distErrorSum += m;
                }
                float lengthDiff = approxLength - measuredLength;
                final float result = distErrorSum + (4*lengthDiff);
                Log.i("RESULT", Float.toString(result));
                Log.i("distErrorSum", Float.toString(distErrorSum));
                Log.i("4*lengthDiff", Float.toString(4*lengthDiff));
                setScore(result);
                btnStart.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                directions.setVisibility(View.GONE);
                Button returnButton = (Button)findViewById(R.id.return_button);
                returnButton.setVisibility(View.VISIBLE);
                ctrlPtsCheckBox.setVisibility(View.GONE);

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("historyDir", Context.MODE_PRIVATE);
                Date date = new Date();
                String filename = date.toString() + ".png";
                File mypath = new File(directory,filename);

                //Save file
                FileOutputStream outputStream = null;

                spiralView.buildDrawingCache();
                Bitmap bm = spiralView.getDrawingCache();

                try {
                    outputStream = new FileOutputStream(mypath);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    try{
                        outputStream.close();
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                /* Per the Android documentation:
                 * "If you call buildDrawingCache() manually without calling setDrawingCacheEnabled(true),
                 * you should cleanup the cache by calling destroyDrawingCache() afterwards." */
                spiralView.destroyDrawingCache();
            }
        });
    }

    //retrieve file
    private void addListenerButtonHistory() {
        final Button btnHistory = (Button) findViewById(R.id.traceHistory);

        btnHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("historyDir", Context.MODE_PRIVATE);

                    try {
                        File file = new File(directory, "myfile.png");
                        Bitmap loadedBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        spiralView.setImageBitmap(loadedBitmap);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("HISTORY:", "MAYBE IT WORKED");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void setNewImage(Bitmap alteredBitmap, Bitmap bmp) {
        canvas = new Canvas(alteredBitmap);
        paint = new Paint();
        paint.setColor(Color.rgb(225,127,39));
        paint.setDither(true);
        paint.setStrokeWidth(20);
        colors = new int[] {Color.BLUE, Color.CYAN, Color.YELLOW, Color.RED};
        matrix = new Matrix();
        path = new Path();
        breaks = 0;
        canvas.drawBitmap(bmp, matrix, paint);
        spiralView.setImageBitmap(alteredBitmap);
    }

    private double[] convertPointFromRelative(double x, double y) {
        return new double[]{(x * widthScale), (y * heightScale)};
    }

    /* Tests a traced point as a potential new closest traced point to each of the control points.
     */
    private void testNewTracedPoint(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
//        Log.i("pos", String.format("%.3f, %.3f", eventX, eventY));
        float ctrlPtX;
        float ctrlPtY;
        double tentativeDist;

        /* clear view -- this shows the "debugging" fancy mode of drawing lines from each control
         * point to the new minimum distance traced point.
         * however this messes with stuff at present, hence why METRIC_DEBUGGING should be off for
         * all actual trials of this */
        if (METRIC_DEBUGGING) {
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.team12spiral);
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                    bmp.getHeight(), bmp.getConfig());
            setNewImage(alteredBitmap, bmp);
        }
        for (int i = 0; i < runningMinControlPointDistances.size(); i++) {
            ctrlPtX = (float) (double) spiralControlPoints.get(i * 2);
            ctrlPtY = (float) (double) spiralControlPoints.get((i * 2) + 1);

            // calculate distance between the traced point and the current control point.
            tentativeDist = Math.sqrt(
                    Math.pow(eventX - ctrlPtX, 2)
                            + Math.pow(eventY - ctrlPtY, 2)
            );
            if (tentativeDist < runningMinControlPointDistances.get(i)) {
                runningMinControlPointDistances.set(i, tentativeDist);
                if (METRIC_DEBUGGING) {
                    canvas.drawLine(ctrlPtX, ctrlPtY, eventX, eventY, distTestPaint);
                }
            }
        }
    }

    private void addTouchListener() {
        spiralView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (noTouchesYet) {
                    addControlPoints();
                    // Start timing
                    noTouchesYet = false;
                    startTime = System.currentTimeMillis();
                }
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downx = event.getX();
                        downy = event.getY();
                        testNewTracedPoint(event);
                        setColorPressure(event.getPressure());
                        if(breaks == 0) {
                            path.moveTo(downx, downy);
                        } else {
                            path.lineTo(downx, downy);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        upx = event.getX();
                        upy = event.getY();
                        testNewTracedPoint(event);
                        setColorPressure(event.getPressure());
                        canvas.drawLine(downx, downy, upx, upy, paint);
                        spiralView.invalidate();
                        path.lineTo(upx, upy);
                        downx = upx;
                        downy = upy;
                        break;
                    case MotionEvent.ACTION_UP:
                        upx = event.getX();
                        upy = event.getY();
                        path.lineTo(upx, upy);
                        setColorPressure(event.getPressure());
                        canvas.drawLine(downx, downy, upx, upy, paint);
                        spiralView.invalidate();
                        breaks++;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }

                return true;
            }

        });
    }

    private void setColorPressure(float pressure){
        if(pressure <= 0.17){
            paint.setColor(colors[0]);
        } else if (pressure <= 0.24){
            paint.setColor(colors[1]);
        } else if (pressure <= 0.27){
            paint.setColor(colors[2]);
        } else if (pressure >= 0.30) {
            paint.setColor(colors[3]);
        }
    }

    private void setScore(float result){
        // TODO consider accounting for "breaks" also
        char publicScore = 0;
        if(result <= 350){
            publicScore = 'A';
        } else if (result <= 450){
            publicScore = 'B';
        } else if (result <= 600){
            publicScore = 'C';
        } else if (result <= 700){
            publicScore = 'D';
        } else {
            publicScore = 'F';
        }
        TextView scoreView = (TextView)findViewById(R.id.traceScore);
        scoreView.append("Score: " + publicScore);
        scoreView.setVisibility(View.VISIBLE);
    }

}
