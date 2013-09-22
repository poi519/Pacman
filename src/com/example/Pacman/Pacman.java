package com.example.Pacman;

import android.util.Log;

public class Pacman extends Movable implements HasRadius {
    final static float BASE_SPEED = 2f;
    private Direction scheduledDirection;

    public Direction getScheduledDirection() {
        return scheduledDirection;
    }

    private void setScheduledDirection(Direction scheduledDirection) {
        this.scheduledDirection = scheduledDirection;
    }

    public float getRadius() {
        return 0.5f;
    }

    public Pacman(Int2 coordinates){
        setCoordinates(coordinates.toFloat2());
        setDirection(Direction.RIGHT);
        setScheduledDirection(Direction.RIGHT);
        setSpeed(BASE_SPEED);
        setMoving(false);
    }

    private boolean tryChangeDirection() {
        Int2 nextCell = getScheduledDirection().nextCell(getCurrentCell());
        Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
        if(nextLocation == Location.WALL) {
            Log.d("Pacman.tryChangeDirection", "Direction cannot be changed because there is wall in direction "
                    + getScheduledDirection());
            setScheduledDirection(getDirection());
            return false;
        } else {
            Log.d("Pacman.tryChangeDirection", "Direction changed to " + getScheduledDirection());
            setDirection(getScheduledDirection());
            setCoordinates(getCurrentCell().toFloat2());
            return true;
        }
    }

    public void scheduleDirectionChange(Direction newDirection) {
        setScheduledDirection(newDirection);
        Log.d("Pacman.scheduleDirectionChange", "scheduledDirection set to " + newDirection);
        if(isMoving()) {
            if(getDirection().isCollinear(newDirection))
                setDirection(newDirection);
        } else {
            Location nextLocation = Game.getInstance().getMap().getLocation(
                    newDirection.nextCell(getCurrentCell()));
            if(nextLocation != Location.WALL) {
                setDirection(newDirection);
                setMoving(true);
            }
        }
    }

    @Override
    public void updateInNewCell(Float2 newCoordinates) {
        eat();
        if(getScheduledDirection() != getDirection()    //direction change was scheduled
           && tryChangeDirection()) {                   //and happened
            //Do nothing
        } else {
            //Check for wall
            Int2 nextCell = getDirection().nextCell(newCoordinates.toInt2());
            Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
            if(nextLocation == Location.WALL) {
                setMoving(false);
                setCoordinates(newCoordinates.toInt2().toFloat2());
            } else {
                setCoordinates(newCoordinates);
            }
        }
    }

    @Override
    public void updateWhileStandingStill() {
        eat();
    }

    public void eat(){
        Int2 coordinates = getCurrentCell();
        GameMap map = Game.getInstance().getMap();
        Location location = map.getLocation(coordinates);
        switch(location){
            case DOT:
            case ENERGIZER:
                map.setLocation(coordinates, Location.SPACE);
                Game.getInstance().increaseScore(location.getScore());
        }
    }
}