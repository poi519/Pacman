package com.example.Pacman;

public class GameMap {
    int topLeftX, topLeftY;
    int cellWidth, cellHeight;
    int width, height;

    Location[][] array;

    public GameMap(int tlx, int tly, int brx, int bry, int width, int height) {
        this.width = width;
        this.height = height;
        topLeftX = tlx;
        topLeftY = tly;
        array = new Location[width][height];
        cellWidth = (brx - tlx) / width;
        cellHeight= (bry - tly) / height;
    }

    public int[] toScreenCoordinates(int x, int y) {
        int[] res = new int[2];
        res[0] = (int) (topLeftX + cellWidth * (x + 0.5));
        res[1] = (int) (topLeftY + cellHeight * (y + 0.5));
        return res;
    }

    void loadFile()
}