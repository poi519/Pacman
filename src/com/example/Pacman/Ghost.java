package com.example.Pacman;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;

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
        this.setDirection(Direction.DOWN);
    }

    @Override
    void updateInNewCell(Float2 newCoordinates) {
        Int2 newCell = newCoordinates.toInt2();
        Direction newDirection;
        final GameMap map = Game.getInstance().getMap();
        if(map.getFreeNeighbourCells(newCell).size() > 2 //More than 2 free neighbours - cell is a crossroad
            || !map.isFree(getDirection().nextCell(newCell))) //Or the current direction is blocked - mb an angle
        {
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
                e.printStackTrace();//TODO decide whether I need this
                // something went wrong, let's choose a direction that minimizes the distance to pacman
                final Int2 pacmanCell = Game.getInstance().getPacman().getCoordinates().toInt2();
                Int2 nextCell = Collections.min(map.getFreeNeighbourCells(newCell), new Comparator<Int2>() {
                    @Override
                    public int compare(Int2 lhs, Int2 rhs) {
                        return (int) Math.signum(GameMap.distance(lhs, pacmanCell) - GameMap.distance(rhs, pacmanCell));
                    }
                });
                try {
                    newDirection = GameMap.findDirectionBetween(newCell, nextCell);
                } catch (CellsAreNotAdjacentException e2) {
                    e2.printStackTrace();
                    newDirection = getDirection();
                }
                setDirection(newDirection);
                setCoordinates(newCell.toFloat2());
            }
        } else
            setCoordinates(newCoordinates);
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
