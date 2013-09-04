package com.example.Pacman;

interface Goal {
    public int[] getCoordinates();
}


class RedGhostGoal implements Goal {
    public int[] getCoordinates() {
        Pacman pacman = Game.getInstance().getPacman();
        int[] result = {Math.round(pacman.getX()), Math.round(pacman.getY())};
        return result;
    }
}