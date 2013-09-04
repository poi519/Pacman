package com.example.Pacman;

interface Goal {
    public int[] getCoordinates();
}

class GhostGoals {
    final public static Goal RED = new Goal(){
        public int[] getCoordinates() {
            Pacman pacman = Game.getInstance().getPacman();
            int[] result = {Math.round(pacman.getX()), Math.round(pacman.getY())};
            return result;
        }
    };

    final public static Goal PINK = new Goal() {
        public int[] getCoordinates() {
            Pacman pacman = Game.getInstance().getPacman();
            Direction dir = pacman.getDirection();
            GameMap map = Game.getInstance().getMap();
            int[] result = {Math.round(pacman.getX()), Math.round(pacman.getY())};
            int[] nextCell;
            for(int i = 0; i < 4; i++) {
                nextCell = dir.nextCell(result[0], result[1]);
                if(map.getLocation(nextCell) == Location.WALL) {
                    break;
                } else {
                    result = nextCell;
                }
            }
            return result;
        }
    };
//
//    final public static Goal RED = new Goal() {
//        public int[] getCoordinates() {
//
//        }
//    };
}