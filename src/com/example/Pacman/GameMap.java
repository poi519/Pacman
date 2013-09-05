package com.example.Pacman;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class NoFreeCellsException extends Exception{}

public class GameMap {
    private int width;
    private int height;

    private Location[][] array;

    public GameMap(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        setArray(new Location[width][height]);
    }

    static GameMap loadInputStream(InputStream fis) {
        GameMap map;
        try {
            map = GameMap.fromDimensionsInStream(fis);
            Log.d("GameMap.loadFile", "Map dimensions are" + map.getWidth() + " " + map.getHeight());
            map.readArrayFromStream(fis);
        } catch(IOException e) {
            map = new GameMap(10, 10);
        } finally {
            try {
                if(fis != null) fis.close();
            } catch(IOException e) {}
        }
        return map;
    }

    private static GameMap fromDimensionsInStream(InputStream fis) throws IOException {
        int i = fis.read();
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        while((char) i != '\n') {
            Log.d("fromDimensionsInStream", Character.toString((char) i));
            bais.write(i);
            i = fis.read();
        }
        String[] dimStrings = bais.toString().split(" ");
        return new GameMap(Integer.parseInt(dimStrings[0]), Integer.parseInt(dimStrings[1]));
    }

    private void readArrayFromStream(InputStream fis) throws IOException {
        int x = 0, y = 0;
        int i = fis.read();
        char c = (char) i;
        while(i != -1) {
            //Log.d("GameMap.readArrayFromStream", "Read " + Character.toString(c));
            if(c == '\n') {
                if(x < getWidth() - 1)
                    for(; x < getWidth(); x++)
                        getArray()[x][y] = Location.SPACE;
                x = 0;
                y++;
            } else {
                getArray()[x][y] = Location.fromChar(c);
                x++;
            }
            i = fis.read();
            c = (char) i;
        }
    }

    public boolean isFree(int[] cell) {
        if(cell[0] >= width || cell[1] >= height || cell[0] < 0 || cell [1] < 0) {
            return false;
        } else {
            Location l = getLocation(cell);
            return !(l == Location.WALL || l == null);
        }
    }

    public Set<List<Integer>> getFreeNeighbourCells(List<Integer> cell) {
        Set<List<Integer>> result = new HashSet<List<Integer>>();
        for(List<Integer> n : getAllNeighbourCells(cell)) {
            int[] c = {n.get(0), n.get(1)};
            if(isFree(c))
                result.add(n);
        }
        return result;
    }

    public Set<List<Integer>> getAllNeighbourCells(List<Integer> cell) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<List<Integer>> result = new HashSet<List<Integer>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(cell.get(0), cell.get(1));
            result.add(Arrays.asList(c[0], c[1]));
        }
        return result;
    }

    public int[] getClosestFreeCell(int[] c) throws NoFreeCellsException {
        List<Integer> cell = Arrays.asList(c[0], c[1]);
        List<List<Integer>> pending = new ArrayList<List<Integer>>();
        pending.addAll(getAllNeighbourCells(cell));

        Set<List<Integer>> evaluated = new HashSet<List<Integer>>();
        List<Integer> current;
        while(!pending.isEmpty()) {
            current = pending.get(0);
            int[] currentArray = {current.get(0), current.get(1)};
            if(isFree(currentArray)) {
                return currentArray;
            } else {
                pending.remove(0);
                evaluated.add(current);
                for(List<Integer> n : getAllNeighbourCells(current)) {
                    if(!evaluated.contains(n))
                        pending.add(n);
                }
            }
            //Log.d("GameMap.getClosestFreeCell", "Evaluated: " + evaluated.size() + " cells");
        }
        throw new NoFreeCellsException();
    }

    public static double distance(int[] c1, int[] c2) {
        return Math.sqrt(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2));
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Location[][] getArray() {
        return array;
    }

    public void setArray(Location[][] array) {
        this.array = array;
    }

    public Location getLocation(int[] cell) {
        return getArray()[cell[0]][cell[1]];
    }

    public void setLocation(int[] cell, Location value) {
        getArray()[cell[0]][cell[1]] = value;
    }
}