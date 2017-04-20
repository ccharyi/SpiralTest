package com.example.spiraltest;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;


public class Team5DrawingView extends View{
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float prevY;
    float prevTheta;
    private float xCenter;
    private float yCenter;
    private ArrayList<Float> radius = new ArrayList<Float>(10000); //Index 0 will be intial condition. Ignore
    private ArrayList<Float> angle = new ArrayList<Float>(10000); //Stores delta Theta between previous and current Index 0 is inital condtion
    int currIndex = 0;
    private float totalAngle = 0;
    private float finalScore = 0;

    public Team5DrawingView(Context context, AttributeSet attributes)
    {
        super(context, attributes);
        set_up_drawing();
        setDrawingCacheEnabled(true);

    }

    public void set_up_drawing()
    {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLUE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        xCenter = size.x/2.0f;
        yCenter = size.y/2.0f;
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        // drawCanvas.drawCircle(xCenter, yCenter, (yCenter*2), paint);
        paint.setColor(Color.BLACK);
        drawCanvas.drawCircle(xCenter, yCenter, xCenter, paint);
        paint.setColor(Color.LTGRAY);
        drawCanvas.drawCircle(xCenter, yCenter, .76f*xCenter, paint);
        paint.setColor(Color.BLACK);
        drawCanvas.drawCircle(xCenter, yCenter, .52f*xCenter, paint);
        paint.setColor(Color.LTGRAY);
        drawCanvas.drawCircle(xCenter, yCenter, .28f*xCenter, paint);
        paint.setColor(Color.GREEN);
        drawCanvas.drawCircle(xCenter, yCenter, .04f*xCenter, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();
        int action = event.getAction();
        TextView scoreText = (TextView)(this.getRootView().findViewById(R.id.tView));
        scoreText.setTextColor(Color.GREEN);
        scoreText.setTextSize(15);
        scoreText.setText("Draw spiral outward from green center.");

        if (action == MotionEvent.ACTION_MOVE)
        {
            drawPath.lineTo(touchX, touchY);
            fillArrays((touchX - xCenter), (touchY - yCenter)*-1);
        }
        else if (action == MotionEvent.ACTION_DOWN)
        {
            currIndex = 0;
            totalAngle = 0;
            finalScore = 0;
            drawPath.reset();
            drawPath.moveTo(touchX, touchY);
        }
        else if (action == MotionEvent.ACTION_UP)
        {
            if(currIndex > 0){
                currIndex--;
            }
            //TextView scoreText = (TextView)(this.getRootView().findViewById(R.id.tView));
            scoreText.setTextColor(Color.RED);
            //Calculate errors based on arrays.
            float max = getMax(radius);
            float slope = max/totalAngle;
            float error = calculateError(slope);
            float maxError = slope*totalAngle*0.5f*totalAngle; //((1/2)h*b)
            finalScore = (1 - (error/maxError))*100.00f;

            if(totalAngle < 4*2*Math.PI) {
                int loops = (int) (totalAngle / (2 * Math.PI));
                scoreText.setText("Invalid number of loops: " + loops + ". Try again");
                currIndex = 0;
                totalAngle = 0;
                finalScore = 0;
                drawPath.reset();
            }else {
                scoreText.setText("Your final score is: " + finalScore + "%.");
                // MainActivity.activity.takeScreenshot1();
                Team5SpiralTestMain.activity.sentToSheets(finalScore);
            }
        }
        invalidate();
        return true;
    }

    public void startNew()
    {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }


    //Right riemann sum integration of error Rexpected = slope*theta
    private float calculateError(float slope){
        float currTheta = 0;
        float error = 0;
        for(int i = 1; i <= currIndex; i++){
            float dtheta = angle.get(i);
            currTheta += dtheta;
            float actualRRight = radius.get(i);
            float actualRLeft = radius.get(i-1);
            float expectedR = slope*currTheta;
            float dRRight = Math.abs(actualRRight - expectedR);
            //float dRLeft = Math.abs(actualRLeft - expectedR);
            //float dR = .5f*(dRLeft + dRRight);
            error += dRRight*dtheta;
        }
        return error;
    }






    //For each position add radius and angle to array
    private void fillArrays(float currX, float currY) {
        float dtheta = 0;
        if(currIndex > 0){
            //Everyone after first
            float currAngle = calcAngle(currX, currY);
            //float dtheta;
            if (currX > 0 && (prevY < 0 && currY > 0)) {
                dtheta = (float) (2 * Math.PI - prevTheta + currAngle);
            }else if(currX > 0 && (prevY > 0 && currY < 0)){
                dtheta = (float) (2 * Math.PI - currAngle + prevTheta);
            }else{
                dtheta = (float) ((10*Math.PI + Math.abs(currAngle - prevTheta)) % (Math.PI * 2));
            }

            radius.add(currIndex, ((float)(Math.sqrt((currX * currX) + (currY * currY)))));
            angle.add(currIndex, dtheta);

            prevTheta = currAngle;
            prevY = currY;
            totalAngle += dtheta;
            currIndex++;
        }else{
            //First one
            radius.add(0,((float)(Math.sqrt((currX * currX) + (currY * currY)))));
            angle.add(0,(calcAngle(currX,currY)));

            prevTheta = angle.get(0);
            currIndex++;
            prevY = currY;
        }

    }

    private float getMax(ArrayList<Float> list){
        float max = 0;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) > max){
                max = list.get(i);
            }
        }
        return max;
    }


    /*
    Returns theta value between [0,2PI] for given position
    */
    private float calcAngle(float x, float y){
        if(x == 0 && y > 0){
            return (float)(0.5f*Math.PI);
        }else if(x == 0 && y < 0){
            return (float) (1.5*Math.PI);
        }else if(y == 0 && x < 0){
            return (float)Math.PI;
        }else if(y == 0 && x > 0){
            return 0;
        }else{
            float theta = (float)Math.abs(Math.atan(y/x));
            if(x > 0 && y > 0){
                return theta;
            }else if(x > 0 && y < 0){
                return (float)((2.0f*Math.PI) - theta);
            }else if(x < 0 && y > 0){
                return (float)(Math.PI - theta);
            }else if(x < 0 && y < 0){
                return (float)(Math.PI + theta);
            }
        }
        return 0;
    }


}