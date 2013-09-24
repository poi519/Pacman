package com.example.Pacman;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private Pacman pacman;
    private GameMap map;
    private Level level;
    private long score;
    public final float REFRESH_RATE;
    private Map<GhostColor, Ghost> ghosts;
    private float time;
    private GameActivity activity;
    private static final Game instance = new Game();

    public GameActivity getActivity() {
        return activity;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public Level getLevel() {
        return level;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private int lives;

    public Map<GhostColor, Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(Map<GhostColor, Ghost> ghosts) {
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
        ghosts = new HashMap<GhostColor, Ghost>();
        REFRESH_RATE = 60;
        time = 0;
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

    public void loadLevel(int n) {
        level = new Level(n);
        loadMap(GameMap.loadInputStream(activity.openTextAsset("maps/", "level1.txt")));
        pacman.setSpeed(Pacman.BASE_SPEED * level.getSpeedFactor());
        for(Ghost g : ghosts.values()) {
            g.setSpeed(Ghost.BASE_SPEED * level.getSpeedFactor());
        }
    }

    public void nextLevel() {
        int n = level.getNumber();
        if(n >= 10) {
            //game over
        } else {
            loadLevel(n + 1);
        }
    }

    public void loadMap(GameMap map) {
        this.map = map;
        ghosts.clear();
        Int2 position;
        for(String name : map.getInitialPositions().keySet()){
            position = map.getInitialPositions().get(name);
            if(name.equals("Pacman")) {
                this.pacman = new Pacman(position);
            } else {
                GhostColor c = GhostColor.valueOf(name);
                ghosts.put(c, new Ghost(c, position));
            }
        }
        AStar.reset();
    }

    public void goToInitialPositions() {
        Float2 position;
        Ghost g;
        for(String name : map.getInitialPositions().keySet()){
            position = map.getInitialPositions().get(name).toFloat2();
            if(name.equals("Pacman")) {
                pacman.setCoordinates(position);
                pacman.setMoving(false);
            } else {
                GhostColor c = GhostColor.valueOf(name);
                g = ghosts.get(c);
                g.setCoordinates(position);
                g.setMoving(false);
                g.setStatus(GhostStatus.WAITING);
                g.setTimeout(5);
            }
        }
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    public void update(){
        float newTime = time + 1 / REFRESH_RATE;
        level.runScript(time, newTime);
        time = newTime;
        pacman.update();
        for(Ghost g : ghosts.values()) {
            g.update();
            GhostStatus st = g.getStatus();
            if(st != GhostStatus.RETURNING
                && GameMap.distance(g.getCoordinates(), pacman.getCoordinates()) < (g.getRadius() + pacman.getRadius())) {
                if(st == GhostStatus.FLEEING) {
                    g.eaten();
                    score += 1000;
                } else {
                    //Pacman's caught
                    lives--;
                    goToInitialPositions();
                    time = 0;
                    break;
                }
            }
        }
    }
}
