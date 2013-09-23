package com.example.Pacman;

abstract class Movable {
    abstract void updateInNewCell(Float2 newCoordinates);
    abstract void updateWhileStandingStill();

    private Float2 coordinates;
    private Int2 cell = new Int2(0, 0);
    private Direction direction;
    private float speed;
    private boolean moving;

    Float2 getCoordinates() {
        return coordinates;
    }

    void setCoordinates(Float2 coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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

    public Int2 getCurrentCell() {
        cell.x = Math.round(coordinates.x);
        cell.y = Math.round(coordinates.y);
        return cell;
    }

    final public void update() {
        float dr = speed * 1f / Game.getInstance().REFRESH_RATE;
        Float2 newCoordinates = new Float2(coordinates);
        if(moving) {
            switch(direction) {
                case UP:
                    newCoordinates.y -= dr; break;
                case DOWN:
                    newCoordinates.y += dr; break;
                case LEFT:
                    newCoordinates.x -= dr; break;
                case RIGHT:
                    newCoordinates.x += dr; break;
            }
            Game.getInstance().getMap().mTorify(newCoordinates);
            if(((int) newCoordinates.x != (int) coordinates.x
                || (int) newCoordinates.y != (int) coordinates.y)) {     //passed through a cell center;
                updateInNewCell(newCoordinates);
            } else {
                coordinates = newCoordinates;
            }
        } else {
            updateWhileStandingStill();
        }
    }
}