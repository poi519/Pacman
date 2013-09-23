package com.example.Pacman;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


class GameThread extends Thread {
    private final long TICK = 16;
    private final int MAX_FRAMES_TO_SKIP = 6;
    private final SurfaceHolder _surfaceHolder; //+final hope this works...
    private final GameView _panel;
    private boolean _run = false;

    public GameThread(SurfaceHolder surfaceHolder, GameView panel) {
        _surfaceHolder = surfaceHolder;
        _panel = panel;
    }

    public void setRunning(boolean run) { //Allow us to stop the thread
        _run = run;
    }

    @Override
    public void run() {
        Canvas c;
        long timePrevFrame = 0;
        long timeDelta, timeNow;
        int framesSkipped;
        while (_run) {     //When setRunning(false) occurs, _run is
            c = null;      //set to false and loop ends, stopping thread

            timeNow = System.currentTimeMillis();
            timeDelta = timeNow - timePrevFrame;
            if (timeDelta < TICK) {
                try {
                    Thread.sleep(TICK - timeDelta);
                } catch (InterruptedException e) {
                    Log.d("Pacman", e.getMessage());
                }
            }
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder) {

                    framesSkipped = 0;
                    while(framesSkipped < MAX_FRAMES_TO_SKIP) {
                        timePrevFrame = System.currentTimeMillis();
                        _panel.game.update();
                        timeNow = System.currentTimeMillis();
                        timeDelta = timeNow - timePrevFrame;
                        if(timeDelta > TICK) {
                            framesSkipped++;
                            timePrevFrame += TICK;
                        } else
                            break;
                    }
                    if(framesSkipped != 0)
                        Log.d("GameThread#run", "" + framesSkipped + " frames skipped");
                    _panel.postInvalidate();
                }
            } finally {
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}