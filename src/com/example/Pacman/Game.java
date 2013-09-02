package com.example.Pacman;

public class Game {
    private Pacman pacman;
    private GameMap map;
    private long score;
    private float refreshRate;
    private static final Game instance = new Game();

    public float getRefreshRate() {
        return refreshRate;
    }

    public long getScore() {
        return score;
    }

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

    private Game() {
        score = 0;
        refreshRate = 60;
    }

    static public Game getInstance() {
        return Game.instance;
    }

    public void loadMap(GameMap map) {
        this.map = map;
        this.pacman = new Pacman(14, 23);
    }

    public void increaseScore(int amount) {
        score += amount;
    }
}
