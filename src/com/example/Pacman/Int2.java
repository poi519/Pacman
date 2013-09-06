package com.example.Pacman;

class Int2 {
    public int x;
    public int y;

    public Int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Int2(Int2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Float2 toFloat2() {
        return new Float2(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Int2)) return false;

        Int2 int2 = (Int2) o;

        if (x != int2.x) return false;
        if (y != int2.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 83 * result + 79 * y;
        return result;
    }
}

class Float2 {
    public float x;
    public float y;

    public Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Float2(Float2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Int2 toInt2() {
        return new Int2(Math.round(x), Math.round(y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Float2)) return false;

        Float2 float2 = (Float2) o;

        if (Float.compare(float2.x, x) != 0) return false;
        if (Float.compare(float2.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 83 * result + 79 * (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
