package com.example.Pacman;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class GameActivity extends Activity {

    Game game;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Move creation of Pacman into Game class
        GameMap map = GameMap.loadFile(this, "level1.txt");
        Pacman pacman = new Pacman(map, 0, 0);
        game = Game.create(map, pacman);
        GameView gameView = new GameView(this);
        setContentView(gameView);

        final GestureDetector gdt = new GestureDetector(this, new FlingListener());
        gameView.setOnTouchListener(new View.OnTouchListener() {
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
}