package com.example.Pacman;

import java.util.Map;
import java.util.Set;

class GhostIsTrappedException extends Exception {}

public interface GhostStrategy {
    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException;
}

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

class SimpleGhostStrategy implements GhostStrategy {
    private Goal goal;

    public SimpleGhostStrategy(Goal goal) {
        this.goal = goal;
    }

    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException {
        GameMap map = Game.getInstance().getMap();
        Set<Map.Entry<Direction, int[]>> neighbours = map.getFreeNeighbourCells(x, y);
        if(!neighbours.isEmpty()) {
            Direction bestDirection = null;
            int[] goalCoordinates = goal.getCoordinates();
            double  currentDistance,
                    bestDistance = 0;
            for(Map.Entry<Direction, int[]> n : neighbours) {
                currentDistance = GameMap.distance(n.getValue(), goalCoordinates);
                if(currentDistance <= bestDistance
                        || bestDistance == 0) {
                    bestDistance = currentDistance;
                    bestDirection = n.getKey();
                }
            }
            return bestDirection;
        } else
            throw new GhostIsTrappedException();
    }
}