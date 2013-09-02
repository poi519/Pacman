package com.example.Pacman;

import android.util.Log;

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
                    bestDistance = -1;
            for(Map.Entry<Direction, int[]> n : neighbours) {
                currentDistance = GameMap.distance(n.getValue(), goalCoordinates);
                Log.d("SimpleGhostStrategy.findBestDirection", "for direction " + n.getKey() +" currentDistance is " + currentDistance);
                if(currentDistance <= bestDistance
                        || bestDistance < 0) {
                    bestDistance = currentDistance;
                    bestDirection = n.getKey();
                }
            }
            Log.d("SimpleGhostStrategy.findBestDirection", "Best Direction is " + bestDirection + " with dist. of " + bestDistance);
            return bestDirection;
        } else
            throw new GhostIsTrappedException();
    }
}