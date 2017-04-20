package com.example.spiraltest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Handles displaying a line to trace, and doing the actual trace
 */

public class Team3TracerView extends View {

    private Paint spiralPaint;
    private Paint userSpiralPaint;
    private Paint tempUserSpiralPaint;
    private Paint boundsPaint;
    private Runnable onFinishListener;
    private Team3Trace userTrace;
    private Team3Trace predrawnTrace;
    private Rect squareBounds;

    private int trace_mode;

    public static final int TRACE_CW = 0;
    public static final int TRACE_CCW = 1;
    public static final int TRACE_FREE = 2;
    public static final int PRACTICE = 3;

    private Runnable onFinish = new Runnable() {
        @Override
        public void run() {
            setEnabled(false);
            if (onFinishListener != null) {
                onFinishListener.run();
            }
        }
    };

    public Team3TracerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        userTrace = new Team3Trace(getWidth()/2, getHeight()/2);
        predrawnTrace = new Team3Trace(getWidth()/2, getHeight()/2);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Team3TracerView, 0, 0);
        try {
            setTraceMode(a.getInteger(R.styleable.Team3TracerView_mode, TRACE_CW));
        } finally {
            a.recycle();
        }

        spiralPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spiralPaint.setColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
        } else {
            //noinspection deprecation
            spiralPaint.setColor(getResources().getColor(R.color.colorPrimary));
        }
        spiralPaint.setStyle(Paint.Style.STROKE);
        spiralPaint.setStrokeWidth(5);

        userSpiralPaint = new Paint(spiralPaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            userSpiralPaint.setColor(getResources().getColor(R.color.colorUser, getContext().getTheme()));
        } else {
            //noinspection deprecation
            userSpiralPaint.setColor(getResources().getColor(R.color.colorUser));
        }

        boundsPaint = new Paint(spiralPaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boundsPaint.setColor(getResources().getColor(R.color.colorExtra, getContext().getTheme()));
        } else {
            //noinspection deprecation
            boundsPaint.setColor(getResources().getColor(R.color.colorExtra));
        }

        tempUserSpiralPaint = new Paint(userSpiralPaint);
    }

    @Override
    protected void onDetachedFromWindow () {
        super.onDetachedFromWindow();
        removeCallbacks(onFinish);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        userTrace.setOrigin(w/2, h/2);
        predrawnTrace.setOrigin(w/2, h/2);
        if (trace_mode == TRACE_CW || trace_mode == TRACE_CCW) {
            createSpiral(predrawnTrace, w, h);
        } else if (trace_mode == TRACE_FREE) {
            if (w > h) {
                int m = (w-h)/2;
                squareBounds = new Rect(m, 0, m+h, h);
            } else {
                int m = (h-w)/2;
                squareBounds = new Rect(0, m, w, m+w);
            }
        }
    }

    @Override
    public void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (trace_mode == TRACE_CW || trace_mode == TRACE_CCW) {
            canvas.drawPath(predrawnTrace.getPaths().get(0), spiralPaint);
        } else if (trace_mode == TRACE_FREE) {
            canvas.drawRect(squareBounds, boundsPaint);
        }

        float step = 1.0f/(userTrace.getNTraces() == 0 ? 1 : userTrace.getNTraces());
        for (int i = 0; i < userTrace.getNTraces(); i += 1) {
            tempUserSpiralPaint.setColor(changeDarkness(userSpiralPaint.getColor(), 0.5f+(step*i)));
            canvas.drawPath(userTrace.getPaths().get(i), tempUserSpiralPaint);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        int finished_timeout = 5000;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                removeCallbacks(onFinish);
                userTrace.add(x, y, System.nanoTime());
                break;
            case MotionEvent.ACTION_MOVE:
                userTrace.add(x, y, System.nanoTime());
                break;
            case MotionEvent.ACTION_UP:
                userTrace.addBreak();
                removeCallbacks(onFinish);
                if (trace_mode != PRACTICE) {
                    postDelayed(onFinish, finished_timeout);
                }
                break;
        }

        invalidate();
        return true;
    }

    private void createSpiral(Team3Trace t, int w, int h) {
        float center_x = w/2;
        float center_y = h/2;

        t.clear();
        long time = System.nanoTime();
        t.add(center_x, center_y, time);

        int numberOfTurns = 5;
        for (float angle_rad = 0f; angle_rad < Math.PI * 2 * numberOfTurns; angle_rad += .1) {
            int a = 15;
            if (trace_mode == TRACE_CW) {
                t.add((float) (center_x + (angle_rad * a) * Math.cos(angle_rad)),
                        (float) (center_y + (angle_rad * a) * Math.sin(angle_rad)), time);
            } else {
                // I don't know, mathematically, why this flips the spiral direction but it does
                t.add((float) (center_x + (angle_rad * a) * Math.sin(angle_rad - (Math.PI / 2))),
                        (float) (center_y + (angle_rad * a) * Math.cos(angle_rad - (Math.PI / 2))), time);
            }
        }
    }

    // factor < 0 for darker, > 0 for lighter
    private int changeDarkness (int c, float factor) {
        int a = Color.alpha(c);
        int r = Math.round(Color.red(c) * factor);
        int g = Math.round(Color.green(c) * factor);
        int b = Math.round(Color.blue(c) * factor);

        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    //just preparing for preprocessing done onSave
    public void onSave (Canvas canvas) {
        super.draw(canvas);
        draw(canvas);
    }

    public float getBaseLength () {
        return new PathMeasure(predrawnTrace.getPaths().get(0), false).getLength();
    }

    public int getTraceMode () {
        return trace_mode;
    }

    public void setTraceMode(int trace_mode) {
        if (trace_mode <= 3 && trace_mode >= 0) {
            this.trace_mode = trace_mode;
        } else {
            this.trace_mode = TRACE_CW;
        }

        createSpiral(predrawnTrace, getWidth(), getHeight());
        userTrace.clear();
        invalidate();
    }

    public void setOnFinishListener (Runnable r) {
        onFinishListener = r;
    }

    public Team3Trace getUserTrace() {
        return userTrace;
    }

    public Team3Trace getPredrawn() {
        return predrawnTrace;
    }
}