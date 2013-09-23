package com.example.Pacman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.*;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    GameThread _thread;
    final Paint paint = new Paint();

    final Game game = Game.getInstance();
    float tlx, tly, brx, bry;

    private int cellWidth, cellHeight;
    private final Int2 aInt2 = new Int2(0, 0),
                       bInt2 = new Int2(0, 0);

    void updateCellDimensions() {
        cellWidth = (int) ((brx - tlx) / game.getMap().getWidth());
        cellHeight = (int) ((bry - tly) / game.getMap().getHeight());
        Log.d("updateCellDimensions", "" + cellWidth + " " + cellHeight);
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public float getCellHeight() {
        return cellHeight;
    }

    public int getScreenRadius(HasRadius smth) {
        return (int) (Math.min(getCellHeight(), getCellWidth()) * smth.getRadius());
    }

    public Int2 toScreenCoordinates(Float2 coordinates) {
        return new Int2((int) (tlx + getCellWidth() * (coordinates.x + 0.5f)),
                        (int) (tly + getCellHeight() * (coordinates.y + 0.5f)));
    }

    public Int2 toScreenCoordinates(float x, float y) {
        return new Int2((int) (tlx + getCellWidth() * (x + 0.5f)),
                        (int) (tly + getCellHeight() * (y + 0.5f)));
    }

    public void mToScreenCoordinates(float x, float y, Int2 int2) {
        int2.x = (int) (tlx + getCellWidth() * (x + 0.5f));
        int2.y = (int) (tly + getCellHeight() * (y + 0.5f));
    }

    public void mToScreenCoordinates(Float2 coordinates, Int2 int2) {
        int2.x = (int) (tlx + getCellWidth() * (coordinates.x + 0.5f));
        int2.y = (int) (tly + getCellHeight() * (coordinates.y + 0.5f));
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

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawMap(canvas);
        for(GhostColor ghostColor : game.getGhosts().keySet()) {
            drawGhost(ghostColor, canvas);
        }
        drawPacman(canvas);
        drawScore(canvas);
        drawLives(canvas);
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
                    case WALL:
                        drawWall(c, i, j);
                        break;
                    case DOT:
                        drawDot(c, i, j);
                        break;
                    case ENERGIZER:
                        drawEnergizer(c, i, j);
                        break;
                }
            }
    }

    public void drawWall(Canvas c, float x, float y) {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        mToScreenCoordinates(x - 0.5f, y - 0.5f, aInt2);
        mToScreenCoordinates(x + 0.5f, y + 0.5f, bInt2);
        c.drawRect(aInt2.x, aInt2.y, bInt2.x, bInt2.y, paint);
    }

    public void drawDot(Canvas c, float x, float y) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        mToScreenCoordinates(x, y, aInt2);
        c.drawCircle(aInt2.x, aInt2.y, getScreenRadius(Location.DOT), paint);
    }

    public void drawEnergizer(Canvas c, float x, float y) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        mToScreenCoordinates(x, y, aInt2);
        c.drawCircle(aInt2.x, aInt2.y, getScreenRadius(Location.ENERGIZER), paint);
    }

    public void drawPacman(Canvas c) {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        Pacman pacman = game.getPacman();
        Float2 coordinates = pacman.getCoordinates();
        mToScreenCoordinates(coordinates.x - 0.5f, coordinates.y - 0.5f, aInt2);
        mToScreenCoordinates(coordinates.x + 0.5f, coordinates.y + 0.5f, bInt2);
        float zeroAngle = toAngle(pacman.getDirection());
        float coordinate = pacman.getDirection().isHorizontal() ? coordinates.x : coordinates.y;
        float mouthAngle = (float) Math.abs(90 * Math.sin(Math.PI * (coordinate - (int) coordinate)));//YEAH!
        c.drawArc(new RectF(aInt2.x, aInt2.y, bInt2.x, bInt2.y),
                zeroAngle + mouthAngle / 2,
                360 - mouthAngle, true, paint);
    }

    public void drawScore(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16);
        canvas.drawText("Your score: " + game.getScore(),
                brx + 16, bry / 2, paint);
    }

    public void drawLives(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16);
        canvas.drawText("Your lives: " + game.getLives(),
                brx + 16, bry / 2 + 32, paint);
    }

    public void drawGhost(GhostColor ghostColor, Canvas canvas) {
        switch (ghostColor) {
            case RED: paint.setColor(Color.RED); break;
            case PINK: paint.setColor(0xFFFFC0CB); break;
            case BLUE: paint.setColor(Color.BLUE); break;
            default: paint.setColor(0xFFFF5500);
        }
        paint.setStyle(Paint.Style.FILL);
        Ghost g = game.getGhosts().get(ghostColor);
        mToScreenCoordinates(g.getCoordinates(), aInt2);
        canvas.drawCircle(aInt2.x, aInt2.y, getScreenRadius(g), paint);
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

        tlx = 0; tly = 0;
        brx = this.getHeight(); bry = this.getHeight();
        updateCellDimensions();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}