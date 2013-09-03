package com.example.Pacman;

import android.util.Log;

import java.util.*;

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

    public static Set<Map.Entry<Direction, int[]>> getFreeNeighbourCells(int x, int y) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<Map.Entry<Direction, int[]>> result = new HashSet<Map.Entry<Direction, int[]>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(x, y);
            if(Game.getInstance().getMap().getLocation(c) != Location.WALL)
                result.add(new AbstractMap.SimpleEntry<Direction, int[]>(d, c));
        }
        return result;
    }

    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException {
        Set<Map.Entry<Direction, int[]>> neighbours = getFreeNeighbourCells(x, y);
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

class AStarStrategy implements GhostStrategy {
    private Goal goal;

    public AStarStrategy(Goal goal) {
        this.goal = goal;
    }

    private static Set<List<Integer>> getFreeNeighbourCells(List<Integer> cell) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<List<Integer>> result = new HashSet<List<Integer>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(cell.get(0), cell.get(1));
            if(Game.getInstance().getMap().getLocation(c) != Location.WALL)
                result.add(Arrays.asList(c[0], c[1]));
        }
        return result;
    }

    private List<Integer> goalNode() {
        int[] ar = goal.getCoordinates();
        return Arrays.asList(ar[0], ar[1]);
    }

    private static double heuristicCostEstimate(List<Integer> start, List<Integer> finish) {
        return Math.abs(finish.get(0) - start.get(0)) + Math.abs(finish.get(1) - start.get(1));
    }

    private static Direction findDirectionBetween(List<Integer> start, List<Integer> finish) {
        int dx = finish.get(0) - start.get(0),
            dy = finish.get(1) - start.get(1);
        switch (dx) {
            case 1 : return Direction.RIGHT;
            case -1: return Direction.LEFT;
            default:
                switch (dy) {
                    case 1 : return Direction.DOWN;
                    case -1: return Direction.UP;
                    default:
                        return Direction.DOWN;
                }
        }
    }

    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException {
        Set<List<Integer>> closedSet = new HashSet<List<Integer>>(),
                            openSet = new HashSet<List<Integer>>();
        List<Integer> start = Arrays.asList(x, y);
        openSet.add(start);

        Map<List<Integer>, List<Integer>> cameFrom = new HashMap<List<Integer>, List<Integer>>();
        final Map<List<Integer>, Double>    gScore = new HashMap<List<Integer>, Double>(), // Cost from start along best known path.
                                            fScore = new HashMap<List<Integer>, Double>(); // Estimated total cost from start to goal through y.
        gScore.put(start, 0.0);    
        fScore.put(start, gScore.get(start) + heuristicCostEstimate(start, goalNode()));

        List<Integer> current;
        double tentativeGScore;
        while(!openSet.isEmpty()) {
            current = Collections.min(openSet, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> lhs, List<Integer> rhs) {
                    return fScore.get(lhs).compareTo(fScore.get(rhs));
                }
            }); //the node in openset having the lowest f_score[] value
            if(current.equals(goalNode()))
                return findDirectionBetween(start, reconstructPath(cameFrom, goalNode()).get(1));//TODO optimize

            openSet.remove(current);
            closedSet.add(current);

            for(List<Integer> neighbour : getFreeNeighbourCells(current)) {
                tentativeGScore = gScore.get(current) + 1;// special case of dist_between(current,neighbor);
                if(closedSet.contains(neighbour) && tentativeGScore >= gScore.get(neighbour)) {
                    continue;
                } else {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, gScore.get(neighbour) + heuristicCostEstimate(neighbour, goalNode()));
                    if(!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
        }
        throw new GhostIsTrappedException();
    }

    private static List<List<Integer>> reconstructPath(Map<List<Integer>, List<Integer>> cameFrom,
                                                       List<Integer> currentNode) {
        List<List<Integer>> p = cameFrom.containsKey(currentNode) ?
                reconstructPath(cameFrom, cameFrom.get(currentNode)) :
                new ArrayList<List<Integer>>();
        p.add(currentNode);
        return p;
    }
}