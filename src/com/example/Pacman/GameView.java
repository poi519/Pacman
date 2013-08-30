package com.example.Pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.*;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final long TICK = 16;
    GameThread _thread;
    Paint paint = new Paint();

    Game game = Game.getInstance();
    float tlx, tly, brx, bry;

    public float getCellWidth() {
        return (brx - tlx) / game.getMap().getWidth();
    }

    public float getCellHeight() {
        return (bry - tly) / game.getMap().getHeight();
    }

    public float getScreenRadius(HasRadius smth) {
        return Math.min(getCellHeight(), getCellWidth()) * smth.getRadius();
    }

    public float[] toScreenCoordinates(float x, float y) {
        float[] res = new float[2];
        res[0] = (tlx + getCellWidth() * (x + 0.5f));
        res[1] = (tly + getCellHeight() * (y + 0.5f));
        return res;
    }

    public float toAngle(Direction dir) {
        switch(dir) {
            case RIGHT: return 0;
            case LEFT: return 180;
            case DOWN: return 90;
            case UP: return 270;
            default: return 0;
        }
    }

    class GameThread extends Thread {
        private final SurfaceHolder _surfaceHolder; //+final hope this works...
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
        drawMap(canvas);
        drawPacman(canvas);
    }

    public void drawPacman(Canvas c) {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        Pacman pacman = game.getPacman();
        //float[] screenCoordinates = toScreenCoordinates(pacman.getX(), pacman.getY());
        //c.drawCircle(screenCoordinates[0], screenCoordinates[1], getScreenRadius(pacman), paint);

        float[] screentl = toScreenCoordinates(pacman.getX() - 0.5f, pacman.getY() - 0.5f);
        float[] screenbr = toScreenCoordinates(pacman.getX() + 0.5f, pacman.getY() + 0.5f);
        float zeroAngle = toAngle(pacman.getDirection());
        float coordinate = pacman.getDirection().isHorizontal() ? pacman.getX() : pacman.getY();
        float mouthAngle = (float) Math.abs(90 * Math.sin(Math.PI * (coordinate - Math.floor(coordinate))));//YEAH!
        c.drawArc(new RectF(screentl[0], screentl[1], screenbr[0], screenbr[1]),
                zeroAngle + mouthAngle / 2,
                360 - mouthAngle, true, paint);
    }

    public void drawMap(Canvas c) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);

        GameMap map = game.getMap();
        Location l;
        for(int i = 0; i < map.getWidth(); ++i)
            for(int j = 0; j < map.getHeight(); ++j) {
                l = map.getArray()[i][j];
                if(l == null) {
                    Log.d("GameView.drawMap", "null location at " + i + " " + j);
                    continue;
                }
                switch(l) {
                    case Wall:
                        drawWall(c, i, j);
                        break;
                    case Dot:
                        drawDot(c, i, j);
                        break;
                    case Energizer:
                        drawEnergizer(c, i, j);
                        break;
                }
            }
    }

    public void drawWall(Canvas c, float x, float y) {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        float[] tl = toScreenCoordinates(x - 0.5f, y - 0.5f);
        float[] br = toScreenCoordinates(x + 0.5f, y + 0.5f);
        c.drawRect(tl[0], tl[1], br[0], br[1], paint);
    }

    public void drawDot(Canvas c, float x, float y) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        float[] sc = toScreenCoordinates(x, y);
        c.drawCircle(sc[0], sc[1], getScreenRadius(Location.Dot), paint);
    }

    public void drawEnergizer(Canvas c, float x, float y) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        float[] sc = toScreenCoordinates(x, y);
        c.drawCircle(sc[0], sc[1], getScreenRadius(Location.Energizer), paint);
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
        brx = this.getHeight(); bry = this.getHeight();

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
                game.getPacman().scheduleDirectionChange(dx > 0 ? Direction.RIGHT : Direction.LEFT);
            } else {
                //Vertical fling
                game.getPacman().scheduleDirectionChange(dy > 0 ? Direction.DOWN : Direction.UP);
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