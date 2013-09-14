package com.example.Pacman;

import java.util.*;

class GhostIsTrappedException extends Exception {}
class AStarFailureException extends Exception {}

public interface GhostStrategy {
    public Direction findBestDirection(Int2 cell) throws GhostIsTrappedException;
}

abstract class AbstractAStarStrategy<Node> {
    abstract Set<Node> getNeighbourNodes(Node cell);
    abstract Node goalNode();
    abstract double heuristicCostEstimate(Node start, Node finish);

    public Node findBestNextNode(Node start) throws AStarFailureException {
        Set<Node> closedSet = new HashSet<Node>(),
                openSet = new HashSet<Node>();
        openSet.add(start);
        Node goalNode = goalNode();

        Map<Node, Node> cameFrom = new HashMap<Node, Node>();
        final Map<Node, Double>    gScore = new HashMap<Node, Double>(), // Cost from start along best known path.
                fScore = new HashMap<Node, Double>(); // Estimated total cost from start to goal through y.
        gScore.put(start, 0.0);
        fScore.put(start, gScore.get(start) + heuristicCostEstimate(start, goalNode));

        Node current;
        double tentativeGScore;
        while(!openSet.isEmpty()) {
            current = Collections.min(openSet, new Comparator<Node>() {
                @Override
                public int compare(Node lhs, Node rhs) {
                    return fScore.get(lhs).compareTo(fScore.get(rhs));
                }
            }); //the node in openset having the lowest f_score[] value
            if(current.equals(goalNode))
                return nextNode(cameFrom, goalNode);

            openSet.remove(current);
            closedSet.add(current);

            for(Node neighbour : getNeighbourNodes(current)) {
                tentativeGScore = gScore.get(current) + 1;// special case of dist_between(current,neighbor);
                if(closedSet.contains(neighbour) && tentativeGScore >= gScore.get(neighbour)) {
                    continue;
                } else {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, gScore.get(neighbour) + heuristicCostEstimate(neighbour, goalNode));
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

class CellsAreNotAdjacentException extends Exception {}

class AStarStrategy extends AbstractAStarStrategy<Int2> implements GhostStrategy {
    private Goal goal;

    public AStarStrategy(Goal goal) {
        this.goal = goal;
    }

    @Override
    Set<Int2> getNeighbourNodes(Int2 cell) {
        return Game.getInstance().getMap().getFreeNeighbourCells(cell);
    }

    @Override
    Int2 goalNode() {
        return goal.getCoordinates();
    }

    @Override
    double heuristicCostEstimate(Int2 start, Int2 finish) {
        return Math.abs(finish.x - start.x) + Math.abs(finish.y - start.y);
    }

    public Direction findBestDirection(Int2 start) throws GhostIsTrappedException {
        try {
            return GameMap.findDirectionBetween(start, findBestNextNode(start));
        } catch (AStarFailureException e) {
            e.printStackTrace();
            throw new GhostIsTrappedException();
        } catch (CellsAreNotAdjacentException e) {
            e.printStackTrace();
            throw new GhostIsTrappedException();
        }
    }
}

class SimpleStrategy implements GhostStrategy {
    private Goal goal;

    public SimpleStrategy(Goal goal) {
        this.goal = goal;
    }

    public Direction findBestDirection(Int2 start) throws GhostIsTrappedException {
        final Int2 goalCell = goal.getCoordinates();
        GameMap map = Game.getInstance().getMap();
        Int2 nextCell = Collections.min(map.getFreeNeighbourCells(start), new Comparator<Int2>() {
            @Override
            public int compare(Int2 lhs, Int2 rhs) {
                return (int) Math.signum(GameMap.distance(lhs, goalCell) - GameMap.distance(rhs, goalCell));
            }
        });
        try {
            return GameMap.findDirectionBetween(start, nextCell);
        } catch (CellsAreNotAdjacentException e2) {
            e2.printStackTrace();
            throw new GhostIsTrappedException();
        }
    }
}