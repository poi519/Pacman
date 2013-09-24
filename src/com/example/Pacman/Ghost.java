package com.example.Pacman;

import android.util.Log;

enum GhostColor {
    RED, PINK, BLUE, ORANGE
}

enum GhostStatus {
    WAITING, CHASING, WANDERING, FLEEING, RETURNING
}

public class Ghost extends Movable implements HasRadius {
    final static float BASE_SPEED = 1.8f;
    final Goal chasingGoal, wanderingGoal;
    private final PathFinder pathFinder = AStar.getFinder();
    private GhostStatus status;
    private float timeout;
    private boolean forceDirectionChange;
    private Int2 initialPosition;

    public float getTimeout() {
        return timeout;
    }

    public void setTimeout(float timeout) {
        this.timeout = timeout;
    }

    public GhostStatus getStatus() {
        return status;
    }

    public void setStatus(GhostStatus status) {
        this.status = status;
    }

    public float getRadius() {
        return 0.5f;
    }

    public Ghost(GhostColor c, Int2 position) {
        this.chasingGoal = GhostChasingGoals.get(c);
        this.wanderingGoal = GhostWanderingGoals.get(c);
        setSpeed(BASE_SPEED);
        status = GhostStatus.WAITING;
        timeout = 5;
        this.setMoving(false);
        initialPosition = position;
        this.setCoordinates(position.toFloat2());
        this.setDirection(Direction.DOWN);
    }

    @Override
    void updateEveryTick() {
        if(status == GhostStatus.FLEEING) {
            timeout = timeout - 1 / Game.getInstance().REFRESH_RATE;
            if(timeout < 0) {
                status = GhostStatus.CHASING;
                forceDirectionChange = true;
            }
        }
    }

    @Override
    void updateInNewCell(Float2 newCoordinates) {
        Int2 newCell = newCoordinates.toInt2();
        Direction newDirection;
        final GameMap map = Game.getInstance().getMap();
        if(map.getFreeNeighbourCells(newCell).size() > 2 //More than 2 free neighbours - cell is a crossroad
            || !map.isFree(getDirection().nextCell(newCell)) //Or the current direction is blocked - mb an angle
            || forceDirectionChange) //Or the status has changed and the direction change is forced
        {
            switch (status) {
                case CHASING:
                    newDirection = pathFinder.findBestDirection(newCell, chasingGoal.getCoordinates());
                    break;
                case WANDERING:
                    newDirection = pathFinder.findBestDirection(newCell, wanderingGoal.getCoordinates());
                    break;
                case FLEEING:
                    newDirection = map.getRandomDirection(newCell);
                    break;
                case RETURNING:
                    if(newCell.equals(initialPosition)) {
                        status = GhostStatus.CHASING;
                        updateInNewCell(newCoordinates);
                    } else {
                        newDirection = pathFinder.findBestDirection(newCell, initialPosition);
                        break;
                    }
                default:
                    //TODO consider blocked directions
                    newDirection = map.getRandomDirection(newCell);
            }
            if(newDirection == null)
                Log.d("Ghost.updateInNewCell", "New direction is null");
            if(newDirection == getDirection()) {
                setCoordinates(newCoordinates);
            } else if (newDirection != null) {
                setDirection(newDirection);
                setCoordinates(newCell.toFloat2());
            } else {
            // something went wrong, let's stop for a while
                setMoving(false);
            }
            forceDirectionChange = false;
        } else
            setCoordinates(newCoordinates);
    }

    @Override
    void updateWhileStandingStill() {
        if(status == GhostStatus.WAITING) {
            timeout -= 1f / Game.getInstance().REFRESH_RATE;
            if(timeout <= 0)
                status = GhostStatus.CHASING;
        } else {
            setMoving(true);
            updateInNewCell(getCoordinates());
        }
    }

    void flee() {
        if(status != GhostStatus.RETURNING)
            status = GhostStatus.FLEEING;
        timeout = Game.getInstance().getLevel().getEnergizerDuration();
        forceDirectionChange = true;
    }

    void eaten() {
        status = GhostStatus.RETURNING;
        forceDirectionChange = true;
    }

    boolean seesPacman() {
        Int2 pacmanCell = Game.getInstance().getPacman().getCurrentCell();
        Int2 currentCell = getCurrentCell();
        if(currentCell == pacmanCell)
            return true;
        Direction dir = GameMap.findDirectionBetween(currentCell, pacmanCell);
        if(dir == null)
            return false;
        GameMap map = Game.getInstance().getMap();
        Location location;
        while(!currentCell.equals(pacmanCell)) {
            location = map.getLocation(currentCell);
            if(location == Location.WALL || location == null)
                return false;
            currentCell = dir.nextCell(currentCell);
        }
        return true;
    }
}
