package com.example.Pacman;

import java.util.*;

class GhostIsTrappedException extends Exception {}
class AStarFailureException extends Exception {}

public interface GhostStrategy {
    public Direction findBestDirection(int x, int y) throws GhostIsTrappedException;
}

abstract class AbstractAStarStrategy<Node> {
    abstract Set<Node> getNeighbourNodes(Node cell);
    abstract Node goalNode();
    abstract double heuristicCostEstimate(Node start, Node finish);

    public Node findBestNextNode(Node start) throws AStarFailureException {
        Set<Node> closedSet = new HashSet<Node>(),
                openSet = new HashSet<Node>();
        openSet.add(start);

        Map<Node, Node> cameFrom = new HashMap<Node, Node>();
        final Map<Node, Double>    gScore = new HashMap<Node, Double>(), // Cost from start along best known path.
                fScore = new HashMap<Node, Double>(); // Estimated total cost from start to goal through y.
        gScore.put(start, 0.0);
        fScore.put(start, gScore.get(start) + heuristicCostEstimate(start, goalNode()));

        Node current;
        double tentativeGScore;
        while(!openSet.isEmpty()) {
            current = Collections.min(openSet, new Comparator<Node>() {
                @Override
                public int compare(Node lhs, Node rhs) {
                    return fScore.get(lhs).compareTo(fScore.get(rhs));
                }
            }); //the node in openset having the lowest f_score[] value
            if(current.equals(goalNode()))
                return nextNode(cameFrom, goalNode());

            openSet.remove(current);
            closedSet.add(current);

            for(Node neighbour : getNeighbourNodes(current)) {
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
        throw new AStarFailureException();
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom,
                                                       Node currentNode) {
        List<Node> p = cameFrom.containsKey(currentNode) ?
                reconstructPath(cameFrom, cameFrom.get(currentNode)) :
                new ArrayList<Node>();
        p.add(currentNode);
        return p;
    }

    private Node nextNode(Map<Node, Node> cameFrom, Node currentNode) {
        Node nextNode = currentNode;
        while(cameFrom.containsKey(currentNode)) {
            nextNode = currentNode;
            currentNode = cameFrom.get(nextNode);
        }
        return nextNode;
    }
}

class AStarStrategy extends AbstractAStarStrategy<List<Integer>> implements GhostStrategy {
    private Goal goal;

    public AStarStrategy(Goal goal) {
        this.goal = goal;
    }

    @Override
    Set<List<Integer>> getNeighbourNodes(List<Integer> cell) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<List<Integer>> result = new HashSet<List<Integer>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(cell.get(0), cell.get(1));
            if(Game.getInstance().getMap().getLocation(c) != Location.WALL)
                result.add(Arrays.asList(c[0], c[1]));
        }
        return result;
    }

    @Override
    List<Integer> goalNode() {
        int[] ar = goal.getCoordinates();
        return Arrays.asList(ar[0], ar[1]);
    }

    @Override
    double heuristicCostEstimate(List<Integer> start, List<Integer> finish) {
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
        try {
            List<Integer> start = Arrays.asList(x, y);
            return findDirectionBetween(start, findBestNextNode(start));
        } catch (AStarFailureException e) {
            e.printStackTrace();
            throw new GhostIsTrappedException();
        }
    }
}