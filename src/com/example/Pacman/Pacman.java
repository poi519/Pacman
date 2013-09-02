package com.example.Pacman;

import android.util.Log;

enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public int[] nextCell(int x, int y) {
        int[] result = {x, y};
        switch(this) {
            case UP: result[1]--; break;
            case DOWN: result[1]++; break;
            case LEFT: result[0]--; break;
            case RIGHT: result[0]++; break;
        }
        return result;
    }

    public boolean isHorizontal() {
        switch(this) {
            case LEFT:
            case RIGHT:
                return true;
            default:
                return false;
        }
    }

    public boolean isCollinear(Direction other) {
        return (this.isHorizontal() == other.isHorizontal());
    }

}

public class Pacman extends Movable implements HasRadius {
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

    public Pacman(int xx, int yy){
        setX(xx);
        setY(yy);
        setDirection(Direction.RIGHT);
        setScheduledDirection(Direction.RIGHT);
        setSpeed(2.0f);
        setMoving(false);
    }

    private boolean tryChangeDirection() {
        int[] nextCell = getScheduledDirection().nextCell(Math.round(getX()), Math.round(getY()));
        Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
        if(nextLocation == Location.WALL) {
            Log.d("Pacman.tryChangeDirection", "Direction cannot be changed because there is wall in direction "
                    + getScheduledDirection());
            setScheduledDirection(getDirection());
            return false;
        } else {
            Log.d("Pacman.tryChangeDirection", "Direction changed to " + getScheduledDirection());
            setDirection(getScheduledDirection());
            setX(Math.round(getX()));
            setY(Math.round(getY()));
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
                    newDirection.nextCell(Math.round(getX()), Math.round(getY())));
            if(nextLocation != Location.WALL) {
                setDirection(newDirection);
                setMoving(true);
            }
        }
    }

    @Override
    public void updateInNewCell(float newX, float newY) {
        eat();
        if(getScheduledDirection() != getDirection()    //direction change was scheduled
           && tryChangeDirection()) {                   //and happened
            //Do nothing
        } else {
            //Check for wall
            int[] nextCell = getDirection().nextCell(Math.round(newX), Math.round(newY));
            Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
            if(nextLocation == Location.WALL) {
                setMoving(false);
                setX(Math.round(newX));
                setY(Math.round(newY));
            } else {
                setX(newX);
                setY(newY);
            }
        }
    }

    @Override
    public void updateWhileStandingStill() {
        eat();
    }

    public void eat(){
        int[] coordinates = {Math.round(getX()), Math.round(getY())};
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