package com.example.Pacman;

enum Location implements HasRadius {
    SPACE, WALL, DOT, ENERGIZER;

    public static Location fromChar(char c) {
        switch (c) {
            case 'w' : return Location.WALL;
            case ' ' : return Location.SPACE;
            case '.' : return Location.DOT;
            case 'e' : return Location.ENERGIZER;
            default: return Location.SPACE;
        }
    }

    public float getRadius() {
        switch(this) {
            case DOT: return 0.1f;
            case ENERGIZER: return 0.25f;
            default: return 0.5f;
        }
    }

    public int getScore() {
        switch(this) {
            case DOT: return 100;
            case ENERGIZER: return 1000;
            default: return 0;
        }
    }
}