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

    private static Set<List<Integer>> getFreeNeighbourCells(int x, int y) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<List<Integer>> result = new HashSet<List<Integer>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(x, y);
            if(Game.getInstance().getMap().getLocation(c) != Location.WALL)
                result.add(Arrays.asList(c[0], c[1]));
        }
        return result;
    }

    private List<Integer> goalNode() {
        int[] ar = goal.getCoordinates();
        return Arrays.asList(ar[0], ar[1]);
    }

    private static int heuristicCostEstimate(List<Integer> start, List<Integer> finish) {
        return Math.abs(finish.get(0) - start.get(0)) + Math.abs(finish.get(1) - start.get(1));
    }

    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException {
        Set<List<Integer>> closedSet = new HashSet<List<Integer>>(),
                            openSet = new HashSet<List<Integer>>();
        List<Integer> start = Arrays.asList(x, y);
        openSet.add(start);

        Map<List<Integer>, List<Integer>> cameFrom = new HashMap<List<Integer>, List<Integer>>();
        final Map<List<Integer>, Integer>   gScore = new HashMap<List<Integer>, Integer>(),
                                            fScore = new HashMap<List<Integer>, Integer>();
        gScore.put(start, 0);    // Cost from start along best known path.
        // Estimated total cost from start to goal through y.
        fScore.put(start, gScore.get(start) + heuristicCostEstimate(start, goalNode()));

        List<Integer> current;
        while(!openSet.isEmpty()) {
            current = Collections.min(openSet, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> lhs, List<Integer> rhs) {
                    return fScore.get(lhs).compareTo(fScore.get(rhs));
                }
            }); //the node in openset having the lowest f_score[] value
            if(current.equals(goalNode()))
                return reconstructPath(cameFrom, goalNode());

            remove current from openset
            add current to closedset
            for each neighbor in neighbor_nodes(current)
                    tentative_g_score := g_score[current] + dist_between(current,neighbor)
            if neighbor in closedset and tentative_g_score >= g_score[neighbor]
            continue

            if neighbor not in closedset or tentative_g_score < g_score[neighbor]
            came_from[neighbor] := current
            g_score[neighbor] := tentative_g_score
            f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, goal)
            if neighbor not in openset
            add neighbor to openset
        }
        throw new GhostIsTrappedException();
    }

    private static List<List<Integer>> reconstructPath(Map<List<Integer>, List<Integer>> cameFrom,
                                                       List<Integer> currentNode) {
        if(cameFrom.containsKey(currentNode)) {
            List<List<Integer> p = reconstructPath(cameFrom, cameFrom.get(currentNode));
            p.add(currentNode);
            return p;
        } else
            return Arrays.asList(currentNode);
    }
}