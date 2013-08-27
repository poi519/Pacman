package com.example.Pacman;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class GameActivity extends Activity {
    Game game = Game.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Move creation of Pacman into Game class
        game.setMap(GameMap.loadFile(this, "level1.txt"));
        game.setPacman(new Pacman(0, 0));
        GameView gameView = new GameView(this);
        setContentView(gameView);


    }

}