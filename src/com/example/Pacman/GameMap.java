package com.example.Pacman;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class GameMap {
    private int width;
    private int height;

    private Location[][] array;

    public GameMap(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        setArray(new Location[width][height]);
    }

    static GameMap loadFile(Context context, String filename) {
        AssetManager am = context.getAssets();
        InputStream fis = null;
        GameMap map = new GameMap(10, 20);
        try {
            fis = am.open(filename);
        } catch(Exception e) {

        } finally {
            try {
                if(fis != null) fis.close();
            } catch(IOException e) {}
        }

        return map;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Location[][] getArray() {
        return array;
    }

    public void setArray(Location[][] array) {
        this.array = array;
    }
}