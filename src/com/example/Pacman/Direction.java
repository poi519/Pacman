package com.example.Pacman;

enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Int2 nextCell(Int2 c) {
        Int2 result = new Int2(c);
        switch(this) {
            case UP: result.y--; break;
            case DOWN: result.y++; break;
            case LEFT: result.x--; break;
            case RIGHT: result.x++; break;
        }
        return Game.getInstance().getMap().mTorify(result);
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

    static Direction random() {
        int i = (int) (Math.random() * 4);
        switch (i) {
            case 0: return UP;
            case 1: return DOWN;
            case 2: return LEFT;
            default: return RIGHT;
        }
    }

    Direction opposite() {
        switch(this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case RIGHT: return LEFT;
            default: return RIGHT;
        }
    }
}
