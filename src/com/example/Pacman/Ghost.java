package com.example.Pacman;

import android.util.Log;

enum GhostStatus {
    WAITING, CHASING
}

public class Ghost extends Movable implements HasRadius {
    GhostStrategy strategy;
    GhostStatus status;
    float waitTimeout;

    public float getRadius() {
        return 0.5f;
    }

    public Ghost(GhostStrategy strategy, Int2 coordinates) {
        this.strategy = strategy;
        setSpeed(2.0f);
        status = GhostStatus.WAITING;
        waitTimeout = 5;
        this.setCoordinates(coordinates.toFloat2());
    }

    @Override
    void updateInNewCell(Float2 newCoordinates) {
        Int2 newCell = newCoordinates.toInt2();
        Direction newDirection;
        try {
            newDirection = strategy.findBestDirection(newCell);
            Log.d("Ghost.updateInNewCell", "New direction is " + newDirection);
            if(newDirection == getDirection()) {
                setCoordinates(newCoordinates);
            } else {
                setDirection(newDirection);
                setCoordinates(newCell.toFloat2());
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
            updateInNewCell(getCoordinates());
        }
    }
}
