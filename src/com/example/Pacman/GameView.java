package com.example.Pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final long TICK = 16;
    GameThread _thread;
    Game game = Game.getInstance();
    float tlx, tly, brx, bry;

    public float cellWidth(GameMap map) {
        return (brx - tlx) / map.getWidth();
    }

    public float cellHeight(GameMap map) {
        return (bry - tly) / map.getHeight();
    }

    public float[] toScreenCoordinates(int x, int y) {
        GameMap map = Game.getInstance().getMap();
        float[] res = new float[2];
        res[0] = (float) (tlx + cellWidth(map) * (x + 0.5));
        res[1] = (float) (tly + cellHeight(map) * (y + 0.5));
        return res;
    }


    class GameThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private GameView _panel;
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

            while (_run) {     //When setRunning(false) occurs, _run is
                c = null;      //set to false and loop ends, stopping thread

                long timeNow = System.currentTimeMillis();
                long timeDelta = timeNow - timePrevFrame;
                if ( timeDelta < TICK){
                    try{
                        Thread.sleep(TICK - timeDelta);
                    }catch(InterruptedException e){
                        Log.d("Pacman", e.getMessage());
                    }
                }
                timePrevFrame = System.currentTimeMillis();

                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.game.getPacman().update();
                        postInvalidate();
                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        game.getPacman().draw(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()
        _thread = new GameThread(getHolder(), this); //Start the thread that
        _thread.setRunning(true);                     //will make calls to
        _thread.start();                              //onDraw()
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            _thread.setRunning(false);                //Tells thread to stop
            _thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {}
    }
}