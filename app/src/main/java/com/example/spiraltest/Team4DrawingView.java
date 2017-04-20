package com.example.spiraltest;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Handler;

public class Team4DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private ArrayList<Point> blackPoints;
    private ArrayList<Point> pathPoints;
    private boolean measured = false;
    private boolean erase=false;
    private Bitmap scaled_pic;
    private int score = 0;

    public Team4DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        setBackgroundResource(R.drawable.team4_ic_noun_745038_cc);
        //setBackgroundResource(R.mipmap.spiral);
    }

    /*Ensure that the view is always a square*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        blackPoints = new ArrayList<Point>();
        pathPoints = new ArrayList<Point>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        if(!measured){
            scaled_pic = prepareToMeasure();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(scaled_pic.getPixel((int)touchX,(int)touchY) != Color.BLACK){
                    score++;
                }
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if(scaled_pic.getPixel((int)touchX,(int)touchY) != Color.BLACK){
                    score++;
                }
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                if(scaled_pic.getPixel((int)touchX,(int)touchY) != Color.BLACK){
                    score++;
                }
                TextView textView;
                Activity parentActivity = (Activity)this.getContext();
                if (parentActivity != null) {
                    textView = (TextView) parentActivity.findViewById(R.id.scoreTextView);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("SCORE: " + score);
                }
                score = 0;
                /*
                pathPoints = new ArrayList<Point>();
                TextView textView = (TextView) findViewById(R.id.score);
                textView.setText("SCORE: "+ score);*/
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void startNew(){
        //set erase true or false
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }



    private Bitmap prepareToMeasure(){
        measured = true;
        //Bitmap icon = drawableToBitmap(this.getBackground());

        //Bitmap icon = drawableToBitmap(this.getBackground());
        Bitmap ic = drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.team4_ic_noun_745038_cc));
        Bitmap icon = Bitmap.createScaledBitmap(ic,this.getWidth(),this.getHeight(),false);
        //Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_noun_745038_cc));
        //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_noun_745038_cc);
        /*
        Log.i("location",(location[0]) + " "+(location[1]));
        for(int i = 0; i < icon.getWidth(); i++) {
            for(int j = 0; j < icon.getHeight(); j++) {
                int color = icon.getPixel(i, j);
                if(Color.BLACK == color){
                    //Log.i("colored",(i+location[0]) + " "+(j+location[1]));
                    Point p = new Point(i,j);
                    blackPoints.add(p);
                    //drawCanvas.drawPoint(p.x,p.y,canvasPaint);
                }
            }
        }
        //Collections.sort(blackPoints, comp);
        Log.i("array",blackPoints.toString());*/
        return icon;
    }

    private int measureDifference(){
        int score = 0;
        //Collections.sort(pathPoints,comp);
        for(Point p : pathPoints){
            Log.i("MeasurePoints",p.x + " "+p.y);
            if(!blackPoints.contains(p)){
                score++;
            }
        }
        Log.i("Score",""+score);
        return score;
    }
    private Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}