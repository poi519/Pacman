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

    public boolean isFree(Int2 cell) {
        if(cell.x >= width || cell.y >= height || cell.x < 0 || cell .y < 0) {
            return false;
        } else {
            Location l = getLocation(cell);
            return !(l == Location.WALL || l == null);
        }
    }

    public Set<Int2> getFreeNeighbourCells(Int2 cell) {
        Set<Int2> result = new HashSet<Int2>();
        for(Int2 n : getAllNeighbourCells(cell)) {
            if(isFree(n))
                result.add(n);
        }
        return result;
    }

    public Set<Int2> getAllNeighbourCells(Int2 cell) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<Int2> result = new HashSet<Int2>();
        for(Direction d : directions) {
            Int2 c = d.nextCell(cell);
            result.add(c);
        }
        return result;
    }

    public Int2 getClosestFreeCell(Int2 cell) throws NoFreeCellsException {
        List<Int2> pending = new ArrayList<Int2>();
        pending.addAll(getAllNeighbourCells(cell));

        Set<Int2> evaluated = new HashSet<Int2>();
        Int2 current;
        while(!pending.isEmpty()) {
            current = pending.get(0);
            if(isFree(current)) {
                return current;
            } else {
                pending.remove(0);
                evaluated.add(current);
                for(Int2 n : getAllNeighbourCells(current)) {
                    if(!evaluated.contains(n))
                        pending.add(n);
                }
            }
            //Log.d("GameMap.getClosestFreeCell", "Evaluated: " + evaluated.size() + " cells");
        }
        throw new NoFreeCellsException();
    }

    public static double distance(Int2 c1, Int2 c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
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
    //TODO add out of bounds checks
    public Location getLocation(Int2 cell) {
        return getArray()[cell.x][cell.y];
    }

    public void setLocation(Int2 cell, Location value) {
        getArray()[cell.x][cell.y] = value;
    }
}