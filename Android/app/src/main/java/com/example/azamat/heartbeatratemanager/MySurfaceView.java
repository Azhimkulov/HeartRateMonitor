package com.example.azamat.heartbeatratemanager;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by azamat on 12.02.17.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private MyThread mMyThread;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMyThread = new MyThread(getHolder());
        mMyThread.setRunning(true);
        mMyThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mMyThread.setRunning(false);

        while(retry) {
            try {
                mMyThread.join();
                retry = false;
            }
            catch (InterruptedException e) {

            }
        }
    }
}



