package com.example.Pacman;

import android.util.Log;

enum GhostStatus {
    WAITING, CHASING
}

public class Ghost extends Movable implements HasRadius {
    GhostStrategy strategy;
    String name;
    GhostStatus status;
    float waitTimeout;

    public float getRadius() {
        return 0.5f;
    }

    public Ghost(String name, GhostStrategy strategy, int x, int y) {
        this.name = name;
        this.strategy = strategy;
        this.setSpeed(2.0f);
        status = GhostStatus.WAITING;
        this.setX(x);
        this.setY(y);
        Log.d("new Ghost", "Cordinates " + this.getX() + " " + this.getY());
    }

    @Override
    void updateInNewCell(float newX, float newY) {
        int cellX = Math.round(newX);
        int cellY = Math.round(newY);
        Direction newDirection;
        try {
            newDirection = strategy.findBestDirection(cellX, cellY);
            Log.d("Ghost.updateInNewCell", "New direction is " + newDirection);
            if(newDirection == getDirection()) {
                setX(newX);
                setY(newY);
            } else {
                setDirection(newDirection);
                setX(cellX);
                setY(cellY);
            }
        } catch(GhostIsTrappedException e) {
            e.printStackTrace();
        }
    }

    @Override
    void updateWhileStandingStill() {
        if(status == GhostStatus.WAITING) {
            waitTimeout -= 1f / Game.getInstance().getRefreshRate();
            if(waitTimeout <= 0)
                status = GhostStatus.CHASING;
        } else {
            setMoving(true);
            updateInNewCell(getX(), getY());
        }
    }
}
