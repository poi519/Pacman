package com.example.Pacman;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Window;

import java.io.IOException;
import java.io.InputStream;

public class GameActivity extends Activity {
    final Game game = Game.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        game.setActivity(this);
        game.loadLevel(0);

        SoundSystem.initSounds(this);

        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public InputStream openTextAsset(String prefix, String filename) {
        AssetManager am = this.getAssets();
        InputStream fis = null;
        try {
            fis = am.open(prefix + filename);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fis;
    }
}