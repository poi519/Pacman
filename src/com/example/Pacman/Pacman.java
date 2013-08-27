package com.example.Pacman;

import android.util.Log;

enum Direction {UP, DOWN, LEFT, RIGHT}

public class Pacman  {
    private int x;
    private int y;
    private Direction direction;
    private int ticks;
    private double speed;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        Log.d("setDirection", "direction set to" + direction.toString());
        this.direction = direction;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Pacman(int xx, int yy){
        setX(xx);
        setY(yy);
        setDirection(Direction.RIGHT);
        setTicks(0);
        setSpeed(2.0);
    }

    public void update() {
        setTicks(getTicks() + 1);
        if(getTicks() / 60 * getSpeed() >= 1.0) {
            setTicks(0);
            switch(getDirection()) {
                case UP:
                    setY(Math.max(0, getY() - 1));
                    break;
                case DOWN:
                    setY(Math.min(Game.getInstance().getMap().getHeight() - 1, getY() + 1));
                    break;
                case LEFT:
                    setX(Math.max(0, getX() - 1));
                    break;
                case RIGHT:
                    setX(Math.min(Game.getInstance().getMap().getWidth() - 1, getX() + 1));
                    break;
            }
        }
    }
}