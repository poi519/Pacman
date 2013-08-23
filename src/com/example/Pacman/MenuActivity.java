package com.example.Pacman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }

    public void startGame(View arg0){
        Log.d("pacman", "start game");
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void quit(View arg0){
        Log.d("pacman", "quit");
    }
}