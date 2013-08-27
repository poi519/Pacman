package com.example.Pacman;

import android.util.Log;

enum Direction {UP, DOWN, LEFT, RIGHT}

public class Pacman  implements HasRadius {
    private float x;
    private float y;
    private Direction direction;
    private float speed;

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

    public void setDirection(Direction direction) {
        Log.d("setDirection", "direction set to" + direction.toString());
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
        setSpeed(2.0f);
    }

    public void update() {
        float dr = getSpeed() * 1f / 60f;
        if(true) {
            switch(getDirection()) {
                case UP:
                    setY(Math.max(0, getY() - dr));
                    break;
                case DOWN:
                    setY(Math.min(Game.getInstance().getMap().getHeight() - 1, getY() + dr));
                    break;
                case LEFT:
                    setX(Math.max(0, getX() - dr));
                    break;
                case RIGHT:
                    setX(Math.min(Game.getInstance().getMap().getWidth() - 1, getX() + dr));
                    break;
            }
        }
    }
}