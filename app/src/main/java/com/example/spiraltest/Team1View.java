package com.example.spiraltest;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


//Using this as a guide for the drawing functionality: https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202

public class Team1View extends View {
    //drawing tool
    protected Path tracePath;
    protected Paint paint;
    protected Paint canvasPaint;
    protected static final int RED= 0xff660000;
    protected static final int BLUE= 0xff8DEEEE;
    protected Canvas drawCanvas;
    protected Bitmap canvasBitmap;
    protected ArrayList<Float> xPath = new ArrayList<Float>();
    protected ArrayList<Float> yPath = new ArrayList<Float>();
    //timer
    protected long startTime = 0;
    protected long stopTime = 0;
    protected double time = 0.00;
    //constants of spiral
    protected double whole = 13.8;
    protected double a = 1;
    protected double b = .15;
    protected int horizontalMovement = 600;
    protected int verticalMovement = 800;
    protected boolean rightHand = true;
    protected int finishTest = 0;
    protected Team1 xxx;

    public Team1View(Context context, AttributeSet attributes) {
        super(context, attributes);
        drawPrep();
        xxx = (Team1) getContext();
    }

    protected void createSpiral(){
        //creates model spiral, we will use the same equation to score
        paint.setColor(RED);

        //These will make drawing smoother
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //Sets width of brush and stroke instead of fill
        paint.setStrokeWidth(50);
        paint.setStyle(Paint.Style.STROKE);
        double seg = (whole * Math.PI)/100;
        double t = 0.0;
        int x;
        int y;
        tracePath.moveTo(horizontalMovement,verticalMovement);
        for (int i = 0;i < 100;i++){
            t = i * seg;
            x = spiralXValues(a,b,t);
            y = spiralYValues(a,b,t);
            tracePath.lineTo(x + horizontalMovement,y + verticalMovement);
        }
        drawCanvas.drawPath(tracePath,paint);
        tracePath.reset();
        paint.setColor(BLUE);
        paint.setStrokeWidth(25);
    }


    //Calculates x values of spiral
    public int spiralXValues(double a, double b, double t){
        Double hold = (a * (Math.pow(Math.E,(b * t))) * Math.cos(t));
        return hold.intValue();
    }

    //calculates y values of spiral
    public int spiralYValues(double a, double b, double t){
        Double hold = (a * (Math.pow(Math.E,(b * t))) * Math.sin(t));
        return hold.intValue();
    }

    protected void drawPrep(){
        //Create new path that will follow the finger as it traces the spiral
        tracePath = new Path();
        //The actual paint that will be on the screen
        paint = new Paint();
        paint.setColor(RED);

        //These will make drawing smoother
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //Sets width of brush and stroke instead of fill
        paint.setStrokeWidth(50);
        paint.setStyle(Paint.Style.STROKE);

        //According to the doc: Dithering affects how colors that are higher precision
        // than the device are down-sampled
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }


    //Called when SpiralView is assigned a size
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        canvasBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        createSpiral();
        paint.setColor(BLUE);
        paint.setStrokeWidth(25);
    }

    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(canvasBitmap, 0,0, canvasPaint);
        canvas.drawPath(tracePath, paint);
    }

    public boolean onTouchEvent(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();

        if(finishTest != 2) {
            switch (event.getAction()) {
                //Captures what happens when your finger MOVES across the screen
                case MotionEvent.ACTION_MOVE:

                    tracePath.lineTo(touchX, touchY);
                    xPath.add(touchX);
                    yPath.add(touchY);
                    break;

                //Captures what happens when your finger is pressed DOWN on the screen
                case MotionEvent.ACTION_DOWN:
                    tracePath.moveTo(touchX, touchY);
                    startTime = System.currentTimeMillis();
                    xPath.add(touchX);
                    yPath.add(touchY);
                    break;

                //Captures what happens when your finger is lifted UP from the screen
                case MotionEvent.ACTION_UP:

                    drawCanvas.drawPath(tracePath, paint);
                    tracePath.reset();
                    //asking for permission to save
                    //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    //saving screen here
                    //loading view
                    View spiralView = findViewById(R.id.drawing);
                    spiralView.setDrawingCacheEnabled(true);
                    Bitmap spiralViewBitMap = spiralView.getDrawingCache();
                    File postSpiralImageFile = new File(Environment.getExternalStorageDirectory() + "/spiral/");
                    postSpiralImageFile.mkdirs();
                    File file = new File(postSpiralImageFile, "spiral" + Long.toString(System.currentTimeMillis()) + ".png");

                    //this line of code simply saves directly to gallery
                    MediaStore.Images.Media.insertImage(getContext().getContentResolver(), spiralViewBitMap, "spiral" + Long.toString(System.currentTimeMillis()) + ".png", "after spiral draw test");
                    //creating new file here
                    try {
                        //starting output stream using spiral image file
                        //postSpiralImageFile.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(file);
                        spiralViewBitMap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.close();
                        System.out.println("did work?");
                    } catch (Throwable exception) {
                        System.out.println("something went wrong :(");
                        exception.printStackTrace();
                    }
                    stopTime = System.currentTimeMillis();
                    time = (stopTime - startTime);
                    time = time / 1000;
                    Team1.textViewObj.setText(Double.toString(time) + " Seconds");
                    Log.v("myTime", Double.toString(time) + "Seconds");
                    int score = scoreSpiral();
                    System.out.println(score);

                    drawCanvas.drawColor(Color.WHITE);

                    createSpiral();
                    break;
                default:
                    return false;
            }

            //Completes the drawing
            invalidate();
        }
        return true;
    }

    public int scoreSpiral(){
        int size = xPath.size();
        double seg = (whole * Math.PI)/size;
        double t = 0.0;
        int actualX;
        int actualY;
        float theirX;
        float theirY;
        float sum = 0;
        //in here we will see how close each location is to where the spiral formula should put it
        for (int i = 0;i < size;i++){
            t = i * seg;
            actualX = spiralXValues(a,b,t) + horizontalMovement;
            actualY = spiralYValues(a,b,t) + verticalMovement;
            theirX = xPath.get(size - i - 1);
            theirY = yPath.get(size - i - 1);
            //Log.v("example",Float.toString(actualX) + " " + Float.toString(theirX));
            sum += Math.abs(actualX - theirX) + Math.abs(actualY - theirY);
        }
        Float s = sum/size;
        int score = s.intValue();
        Log.v("score",Integer.toString(score));
        xxx.runFromFragment(score, rightHand);
        rightHand = false;
        finishTest++;

        return score;
    }


}