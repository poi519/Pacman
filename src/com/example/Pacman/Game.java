package com.example.Pacman;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private Pacman pacman;
    private GameMap map;
    private long score;
    private float refreshRate;
    private Map<String, Ghost> ghosts;
    private static final Game instance = new Game();

    public Map<String, Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(Map<String, Ghost> ghosts) {
        this.ghosts = ghosts;
    }

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
        this.ghosts = new HashMap<String, Ghost>();
        this.ghosts.put("Blinky", new Ghost(new AStarStrategy(GhostGoals.RED), 15, 13));
        this.ghosts.put("Pinky", new Ghost (new AStarStrategy(GhostGoals.PINK), 16, 13));
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    public void update(){
        pacman.update();
        for(Ghost g : ghosts.values()) {
            g.update();
        }
    }
}
