package com.example.Pacman;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    final public static Goal BLUE = new Goal() {
        public int[] getCoordinates() {
            Pacman pacman = Game.getInstance().getPacman();
            Ghost blinky = Game.getInstance().getGhosts().get("Blinky");
            int[] endA = {Math.round(blinky.getX()), Math.round(blinky.getY())};

            int[] center = {Math.round(pacman.getX()), Math.round(pacman.getY())};
            for(int i = 0; i < 2; i++) {
                center = pacman.getDirection().nextCell(center[0], center[1]);
            }

            int[] endB = {2 * center[0] - endA[0], 2 * center[1] - endA[0]};
            GameMap m = Game.getInstance().getMap();

            if(endB[0] < 0) endB[0] = 0;
            if(endB[0] >= m.getWidth()) endB[0] = m.getWidth();
            if(endB[1] < 0) endB[1] = 0;
            if(endB[1] >= m.getHeight()) endB[1] = m.getHeight();

            if(!m.isFree(endB)) {
                try {
                    endB = m.getClosestFreeCell(endB);
                } catch(NoFreeCellsException e) {
                    e.printStackTrace();
                }
            }
            //Log.d("BLUE GHOST GOAL", "is " + endB[0] + " " + endB[1]);
            return endB;
        }
    };

}