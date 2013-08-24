package com.example.Pacman;

public class DisplayHelper {
    float tlx, tly, brx, bry;

    public DisplayHelper(float x1, float y1, float x2, float y2) {
        tlx = x1;
        tly = y1;
        brx = x2;
        bry = y2;
    }

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
}
