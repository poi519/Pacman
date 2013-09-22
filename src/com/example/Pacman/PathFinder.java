package com.example.Pacman;

import android.util.Log;

import java.util.*;

public interface PathFinder {
    public Direction findBestDirection(Int2 from, Int2 to);
}

abstract class AbstractAStarFinder<Node> {
    abstract Set<Node> getNeighbourNodes(Node cell);
    abstract double heuristicCostEstimate(Node start, Node finish);

    public Node findBestNextNode(Node start, Node goalNode) {
        Set<Node> closedSet = new HashSet<Node>(),
                openSet = new HashSet<Node>();
        openSet.add(start);

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
        return null;//haha
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

class AStarFinder extends AbstractAStarFinder<Int2> implements PathFinder {
    private final Map<Pair<Int2, Int2>, Direction> memo = new HashMap<Pair<Int2, Int2>, Direction>();

    @Override
    Set<Int2> getNeighbourNodes(Int2 cell) {
        return Game.getInstance().getMap().getFreeNeighbourCells(cell);
    }

    @Override
    double heuristicCostEstimate(Int2 start, Int2 finish) {
        return Math.abs(finish.x - start.x) + Math.abs(finish.y - start.y);
    }

    public Direction findBestDirection(Int2 start, Int2 finish){
        Pair<Int2, Int2> p = new Pair<Int2, Int2>(start, finish);
        Direction result;
        if(memo.containsKey(p)) {
            Log.d("AStar.Finder#findBestDirection", "Used memoized result");
            return memo.get(p);
        } else {
            result = GameMap.findDirectionBetween(start, findBestNextNode(start, finish));
            memo.put(p, result);
            return result;
        }
    }

    public void clearMemo() {
        memo.clear();
    }
}

class AStar {
    static PathFinder getFinder() {
        return finder;
    }

    static void reset() {
        finder.clearMemo();
    }

    private static final AStarFinder finder = new AStarFinder();
}