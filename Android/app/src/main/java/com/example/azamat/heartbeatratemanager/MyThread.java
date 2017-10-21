package com.example.azamat.heartbeatratemanager;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import static com.example.azamat.heartbeatratemanager.GlobalVar.visual;
import static com.example.azamat.heartbeatratemanager.GlobalVar.visual;

/**
 * Created by azamat on 12.02.17.
 */

public class MyThread extends Thread {

    private final int REDRAW_TIME    = 10;
    private final int ANIMATION_TIME = 1_500;

    private final SurfaceHolder mSurfaceHolder;

    private boolean mRunning;
    private long    mStartTime;
    private long    mPrevRedrawTime;

    private Paint mPaint;
    private ArgbEvaluator mArgbEvaluator;

    float start = 0;
    boolean firstRate = true;
    float previousRate;

    public MyThread(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        mRunning = false;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mArgbEvaluator = new ArgbEvaluator();
    }

    public void setRunning(boolean running) {
        mRunning = running;
        mPrevRedrawTime = getTime();
    }

    public long getTime() {
        return System.nanoTime() / 1_000_000;
    }

    @Override
    public void run() {
        Canvas canvas;
        mStartTime = getTime();

        while (mRunning) {
            long curTime = getTime();
            long elapsedTime = curTime - mPrevRedrawTime;
            if (elapsedTime < REDRAW_TIME)
                continue;

            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    draw(canvas);
                }
            }
            catch (NullPointerException e) {}
            finally {
                if (canvas != null)
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
            }

            mPrevRedrawTime = curTime;
        }
    }

    private void draw(Canvas canvas) {

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float end = Float.parseFloat(String.valueOf(width));
        float maxY = Float.parseFloat(String.valueOf(height));

        canvas.drawColor(Color.BLACK);

        int centerX = width / 2;
        int centerY = height / 2;

        float maxSize = Math.min(width, height) / 2;

        float fraction = (float) visual;

        int color = (int) mArgbEvaluator.evaluate(fraction, Color.RED, Color.BLACK);
        mPaint.setColor(color);

//        canvas.drawCircle(centerX, centerY, fraction/maxSize, mPaint);
//        canvas.drawCircle(centerX, centerY, fraction/2, mPaint);

//        canvas.drawPoint(centerX, fraction/2, mPaint);
        float step = 5;
        float cut = fraction*2;
        if (firstRate)
        {
            if (cut>=maxY)
            {
                canvas.drawLine(start, centerY, start+step, centerY, mPaint);
                previousRate=cut;
                firstRate=false;
            }
            else
            {
                canvas.drawLine(start, centerY, start+step, cut, mPaint);
                previousRate=cut;
                firstRate=false;
            }

        }
        else
        {
            if (cut>=maxY)
            {
                canvas.drawLine(start, previousRate, start+step, centerY, mPaint);
                previousRate=cut;
            }
            else
            {
                canvas.drawLine(start, previousRate, start+step, cut, mPaint);
                previousRate=cut;
            }
        }

        if (start<end)
        {
            start=start+step;
        }
        else
        {
            start = 0;
        }
    }
}
