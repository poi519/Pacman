package com.example.Pacman;

abstract class Movable {
    abstract void updateInNewCell(float newX, float newY);
    abstract void updateWhileStandingStill();

    private float x;
    private float y;
    private Direction direction;
    private float speed;
    private boolean moving;

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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

    protected void setDirection(Direction direction) {
       this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    final public void update() {
        float dr = speed * 1f / Game.getInstance().getRefreshRate();
        float newX = x;
        float newY = y;
        if(moving) {
            switch(direction) {
                case UP:
                    newY = y - dr; break;
                case DOWN:
                    newY = y + dr; break;
                case LEFT:
                    newX = x - dr; break;
                case RIGHT:
                    newX = x + dr; break;
            }
            if(((int) newX != (int) x
                    || (int) newY != (int) y)) {     //passed through a cell center;
                updateInNewCell(newX, newY);
            } else {
                x = newX;
                y = newY;
            }
        } else {
            updateWhileStandingStill();
        }
    }
}