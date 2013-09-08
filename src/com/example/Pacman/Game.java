package com.example.Pacman;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private Pacman pacman;
    private GameMap map;
    private long score;
    public final float REFRESH_RATE;
    private Map<String, Ghost> ghosts;
    private static final Game instance = new Game();

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private int lives;

    public Map<String, Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(Map<String, Ghost> ghosts) {
        this.ghosts = ghosts;
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
        ghosts = new HashMap<String, Ghost>();
        REFRESH_RATE = 60;
        setupNewGame();
    }

    static public Game getInstance() {
        return Game.instance;
    }

    public void setupNewGame() {
        score = 0;
        ghosts.clear();
        lives = 3;
    }


    public void loadMap(GameMap map) {
        this.map = map;
        goToInitialPositions();
    }

    public void goToInitialPositions() {
        ghosts.clear();
        Int2 position;
        for(String name : map.getInitialPositions().keySet()){
            position = map.getInitialPositions().get(name);
            if(name.equals("Pacman")) {
                this.pacman = new Pacman(position);
            } else {
                ghosts.put(name, Ghost.makeGhost(name, position));
            }
        }
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    public void update(){
        pacman.update();
        for(Ghost g : ghosts.values()) {
            g.update();
            if(g.status != GhostStatus.FLEEING
                && GameMap.distance(g.getCoordinates(),
                    pacman.getCoordinates()) < (g.getRadius() + pacman.getRadius())) {
                //Pacman's caught
                lives--;
                goToInitialPositions();
                break;
            }
        }
    }
}
