package com.example.Pacman;

import android.util.Log;

enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public float[] nextCell(float x, float y) {
        float x_round = Math.round(x);
        float y_round = Math.round(y);

        float[] result = {x_round, y_round};
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

public class Pacman  implements HasRadius {
    private float x;
    private float y;
    private Direction direction;
    private Direction scheduledDirection;
    private float speed;
    private boolean moving;

    public Direction getScheduledDirection() {
        return scheduledDirection;
    }

    private void setScheduledDirection(Direction scheduledDirection) {
        this.scheduledDirection = scheduledDirection;
    }


    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public float getRadius() {
        return 0.5f;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    private void setDirection(Direction direction) {
        //Log.d("setDirection", "direction set to" + direction.toString());
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Pacman(int xx, int yy){
        setX(xx);
        setY(yy);
        setDirection(Direction.RIGHT);
        setScheduledDirection(Direction.RIGHT);
        setSpeed(2.0f);
        setMoving(false);
    }

    private boolean changeDirection() {
        float[] nextCell = getScheduledDirection().nextCell(getX(), getY());
        Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
        if(nextLocation == Location.Wall) {
            Log.d("Pacman.changeDirection", "Direction cannot be changed because there is wall in direction "
                    + getScheduledDirection());
            setScheduledDirection(getDirection());
            return false;
        } else {
            Log.d("Pacman.changeDirection", "Direction changed to " + getScheduledDirection());
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
            Location nextLocation = Game.getInstance().getMap().getLocation(newDirection.nextCell(getX(), getY()));
            if(nextLocation != Location.Wall) {
                setDirection(newDirection);
                setMoving(true);
            }
        }
    }

    public void update() {
        float dr = getSpeed() * 1f / 60f;
        float newX = getX();
        float newY = getY();
        if(isMoving()) {
            switch(getDirection()) {
                case UP:
                    newY = Math.max(0, getY() - dr);
                    break;
                case DOWN:
                    newY = Math.min(Game.getInstance().getMap().getHeight() - 1, getY() + dr);
                    break;
                case LEFT:
                    newX = Math.max(0, getX() - dr);
                    break;
                case RIGHT:
                    newX = Math.min(Game.getInstance().getMap().getWidth() - 1, getX() + dr);
                    break;
            }
            //Check for wall
            if((Math.floor(newX) != Math.floor(getX())
                || Math.floor(newY) != Math.floor(getY()))) {     //passed through a cell center;
                if(getScheduledDirection() != getDirection())    //direction change was scheduled
                    if(changeDirection())
                        return;

                float[] nextCell = getDirection().nextCell(newX, newY);
                Location nextLocation = Game.getInstance().getMap().getLocation(nextCell);
                Log.d("Pacman.update", "Next location is " + nextLocation);
                if(nextLocation == Location.Wall) {
                    setMoving(false);
                    setX(Math.round(newX));
                    setY(Math.round(newY));
                    return;
                }
            }
            setX(newX);
            setY(newY);
        }
    }
}