package com.example.Pacman;

enum Location implements HasRadius {
    Space, Wall, Dot, Energizer;

    public static Location fromChar(char c) {
        switch (c) {
            case 'w' : return Location.Wall;
            case ' ' : return Location.Space;
            case '.' : return Location.Dot;
            case 'e' : return Location.Energizer;
            default: return Location.Space;
        }
    }

    public float getRadius() {
        switch(this) {
            case Dot: return 0.1f;
            case Energizer: return 0.25f;
            default: return 0.5f;
        }
    }
}