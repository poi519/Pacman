package com.example.Pacman;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

interface Goal {
    public Int2 getCoordinates();
}

class GhostChasingGoals {
    static Goal get(GhostColor c) {
        switch (c) {
            case RED: return RED;
            case PINK: return PINK;
            case BLUE: return BLUE;
            default: return ORANGE;
        }
    }

    final private static Goal RED = new Goal(){
        @Override
        public Int2 getCoordinates() {
            return Game.getInstance().getPacman().getCoordinates().toInt2();
        }
    };

    final private static Goal PINK = new Goal() {
        @Override
        public Int2 getCoordinates() {
            //If sees pacman attacks him.
            //Otherwise goes to the first crossroad on pacman's way (1st rank crossroad)
            Pacman pacman = Game.getInstance().getPacman();
            Ghost pink = Game.getInstance().getGhosts().get(GhostColor.PINK);
            if(pink.seesPacman())
                return pacman.getCoordinates().toInt2();
            Direction dir = pacman.getDirection();
            GameMap map = Game.getInstance().getMap();
            return map.findNextCrossroad(pacman.getCoordinates(), dir);
        }
    };

    final private static Goal BLUE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            //If sees pacman attacks him.
            //Otherwise finds the closest of possible 2nd rank crossroads and goes there
            //If already there attacks first rank crossroad
            GameMap map = Game.getInstance().getMap();
            Pacman pacman = Game.getInstance().getPacman();
            Ghost blue = Game.getInstance().getGhosts().get(GhostColor.BLUE);

            if(blue.seesPacman())
                return pacman.getCoordinates().toInt2();

            final Int2 blueCell = blue.getCoordinates().toInt2();
            Int2 crossroad1 = PINK.getCoordinates();
            Set<Int2> crossroads2 = new HashSet<Int2>();
            Direction dir;
            for(Int2 n : map.getFreeNeighbourCells(crossroad1)) {
                dir = GameMap.findDirectionBetween(crossroad1, n);
                if(dir == pacman.getDirection().opposite()) continue;
                crossroads2.add(map.findNextCrossroad(n.toFloat2(), dir));
            }
            Int2 result = Collections.min(crossroads2, new Comparator<Int2>() {
                @Override
                public int compare(Int2 lhs, Int2 rhs) {
                    Double  d1 = GameMap.distance(lhs, blueCell),
                            d2 = GameMap.distance(rhs, blueCell);
                    return d1.compareTo(d2);
                }
            });

            if(result.equals(blueCell)) result = crossroad1;
            return result;
        }
    };

    final private static Goal ORANGE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            //Same as BLUE but attacks the crossroad that is most distant from BLUE ghost;
            GameMap map = Game.getInstance().getMap();
            Pacman pacman = Game.getInstance().getPacman();
            Ghost orange = Game.getInstance().getGhosts().get(GhostColor.ORANGE);

            if(orange.seesPacman())
                return pacman.getCoordinates().toInt2();

            final Int2 blueCell = Game.getInstance().getGhosts().get(GhostColor.BLUE).getCoordinates().toInt2();
            Int2 crossroad1 = PINK.getCoordinates();
            Set<Int2> crossroads2 = new HashSet<Int2>();
            Direction dir;
            for(Int2 n : map.getFreeNeighbourCells(crossroad1)) {
                dir = GameMap.findDirectionBetween(crossroad1, n);
                if(dir == pacman.getDirection().opposite()) continue;
                crossroads2.add(map.findNextCrossroad(n.toFloat2(), dir));
            }
            Int2 result = Collections.min(crossroads2, new Comparator<Int2>() {
                @Override
                public int compare(Int2 lhs, Int2 rhs) {
                    Double  d1 = GameMap.distance(lhs, blueCell),
                            d2 = GameMap.distance(rhs, blueCell);
                    return d2.compareTo(d1);
                }
            });

            if(result.equals(orange.getCoordinates().toInt2())) result = crossroad1;
            return result;
        }
    };
}

class GhostWanderingGoals {
    static Goal get(GhostColor c) {
        switch (c) {
            case RED: return RED;
            case PINK: return PINK;
            case BLUE: return BLUE;
            default: return ORANGE;
        }
    }

    final private static Goal RED = new Goal(){
        @Override
        public Int2 getCoordinates() {
            return new Int2(1, 1);
        }
    };

    final private static Goal PINK = new Goal() {
        @Override
        public Int2 getCoordinates() {
            GameMap map = Game.getInstance().getMap();
            return new Int2(map.getWidth() - 2, 1);
        }
    };

    final private static Goal BLUE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            GameMap map = Game.getInstance().getMap();
            return new Int2(map.getWidth() - 2, map.getHeight() - 2);
        }
    };

    final private static Goal ORANGE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            GameMap map = Game.getInstance().getMap();
            return new Int2(1, map.getHeight() - 2);
        }
    };
}