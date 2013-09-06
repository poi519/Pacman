package com.example.Pacman;

class Int2 {
    public int x;
    public int y;

    public Int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Float2 toFloat2() {
        return new Float2(x, y);
    }
}

class Float2 {
    public float x;
    public float y;

    public Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Int2 toInt2() {
        return new Int2(Math.round(x), Math.round(y));
    }
}
