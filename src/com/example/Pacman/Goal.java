package com.example.Pacman;

interface Goal {
    public Int2 getCoordinates();
}

class GhostGoals {
    final public static Goal RED = new Goal(){
        @Override
        public Int2 getCoordinates() {
            return Game.getInstance().getPacman().getCoordinates().toInt2();
        }
    };

    final public static Goal PINK = new Goal() {
        @Override
        public Int2 getCoordinates() {
            Pacman pacman = Game.getInstance().getPacman();
            Direction dir = pacman.getDirection();
            GameMap map = Game.getInstance().getMap();
            Int2 result = pacman.getCoordinates().toInt2();
            Int2 nextCell;
            for(int i = 0; i < 4; i++) {
                nextCell = dir.nextCell(result);
                if(map.getLocation(nextCell) == Location.WALL) {
                    break;
                } else {
                    result = nextCell;
                }
            }
            return result;
        }
    };

    final public static Goal BLUE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            Pacman pacman = Game.getInstance().getPacman();
            Ghost blinky = Game.getInstance().getGhosts().get("Blinky");
            Int2 endA = blinky.getCoordinates().toInt2();

            Int2 center = pacman.getCoordinates().toInt2();
            for(int i = 0; i < 2; i++) {
                center = pacman.getDirection().nextCell(center);
            }

            Int2 endB = new Int2(2 * center.x - endA.x, 2 * center.y - endA.y);
            GameMap m = Game.getInstance().getMap();

            if(endB.x < 0) endB.x = 0;
            if(endB.x >= m.getWidth()) endB.x = m.getWidth() - 1;
            if(endB.y < 0) endB.y = 0;
            if(endB.y >= m.getHeight()) endB.y = m.getHeight() - 1;

            if(!m.isFree(endB)) {
                endB = m.getClosestFreeCell(endB);
            }
            //Log.d("BLUE GHOST GOAL", "is " + endB[0] + " " + endB[1]);
            return endB;
        }
    };

    final public static Goal ORANGE = new Goal() {
        @Override
        public Int2 getCoordinates() {
            Float2 pacmanCoordinates = Game.getInstance().getPacman().getCoordinates();
            Float2 clydeCoordinates = Game.getInstance().getGhosts().get("Clyde").getCoordinates();
            if(GameMap.distance(pacmanCoordinates, clydeCoordinates) > 9)
                return pacmanCoordinates.toInt2();
            else {
                GameMap map = Game.getInstance().getMap();
                return map.getClosestFreeCell(new Int2(map.getWidth() - 1, map.getHeight() - 1));
            }
        }
    };
}