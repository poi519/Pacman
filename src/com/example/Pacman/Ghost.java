package com.example.Pacman;

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

    public Ghost(String name, GhostStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        status = GhostStatus.WAITING;
    }

    @Override
    void updateInNewCell(float newX, float newY) {
        int cellX = Math.round(newX);
        int cellY = Math.round(newY);
        Direction newDirection;
        try {
            newDirection = strategy.findBestDirection(cellX, cellY);
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
            updateInNewCell(getX(), getY());
        }
    }
}
