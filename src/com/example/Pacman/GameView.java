package com.example.Pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.*;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final long TICK = 16;
    GameThread _thread;
    Paint paint = new Paint();

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
        drawPacman(canvas);
    }

    public void drawPacman(Canvas c) {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        Pacman pacman = game.getPacman();
        float[] screenCoordinates = toScreenCoordinates(pacman.getX(), pacman.getY());
        c.drawCircle(screenCoordinates[0], screenCoordinates[1], 20, paint);
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

        tlx = 10; tly = 10;
        brx = 810; bry = 810;

        final GestureDetector gdt = new GestureDetector(this.getContext(), new FlingListener());
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
    }

    class FlingListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("onFling", "Entering method");
            float dx = e2.getX() - e1.getX();
            float dy = e2.getY() - e1.getY();
            Log.d("onFling", "dx, dy = " + dx + " " + dy);
            if(Math.abs(dx) > Math.abs(dy)) {
                //Horizontal fling
                game.getPacman().setDirection(dx > 0 ? Direction.RIGHT : Direction.LEFT);
            } else {
                //Vertical fling
                game.getPacman().setDirection(dy > 0 ? Direction.DOWN : Direction.UP);
            }
            return true;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            _thread.setRunning(false);                //Tells thread to stop
            _thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {}
    }
}