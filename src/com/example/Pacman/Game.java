package com.example.Pacman;

public class Game {
    private Pacman pacman;
    private GameMap map;

    private static final Game instance = new Game();

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

    private Game() {}

    static public Game getInstance() {
        return Game.instance;
    }

    static public Game create(GameMap m, Pacman p) {
        Game.getInstance().setMap(m);
        Game.getInstance().setPacman(p);
        return Game.getInstance();
    }
}
