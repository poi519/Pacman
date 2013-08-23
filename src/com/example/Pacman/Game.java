package com.example.Pacman;

public class Game {
    private Pacman pacman;
    private GameMap map;

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public Pacman getPacman() {
        return pacman;
    }

    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }


    public Game(GameMap m, Pacman p) {
        pacman = p;
        map = m;
    }
}
