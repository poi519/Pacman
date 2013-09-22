package com.example.Pacman;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameMap {
    private int width;
    private int height;
    private Map<String, Int2> initialPositions;
    private Location[][] array;

    public Map<String, Int2> getInitialPositions() {
        return initialPositions;
    }

    public void setInitialPositions(Map<String, Int2> initialPositions) {
        this.initialPositions = initialPositions;
    }

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        array = new Location[width][height];
        initialPositions = new HashMap<String, Int2>();
    }

    static GameMap loadInputStream(InputStream fis) {
        GameMap map;
        try {
            map = GameMap.fromDimensionsInStream(fis);
            Log.d("GameMap.loadFile", "Map dimensions are" + map.getWidth() + " " + map.getHeight());
            for(int i = 0; i < 5; i++)
                map.readInitialPositionFromStream(fis);
            map.readArrayFromStream(fis);
        } catch(IOException e) {
            map = new GameMap(10, 10);
        } finally {
            try {
                if(fis != null) fis.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
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

    private void readInitialPositionFromStream(InputStream fis) throws IOException {
        int i = fis.read();
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        while((char) i != '\n') {
            Log.d("fromDimensionsInStream", Character.toString((char) i));
            bais.write(i);
            i = fis.read();
        }
        String[] dimStrings = bais.toString().split(" ");
        initialPositions.put(dimStrings[0],
                new Int2(Integer.parseInt(dimStrings[1]), Integer.parseInt(dimStrings[2])));
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

    public Int2 getClosestFreeCell(Int2 cell) {
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
        return cell;
    }

    public static Direction findDirectionBetween(Int2 start, Int2 finish) {
        int dx = finish.x - start.x,
            dy = finish.y - start.y;
        if(dx * dy != 0)
            return null;
        if(dx > 0)
            return Direction.RIGHT;
        else if(dx < 0)
            return Direction.LEFT;
        else if(dy > 0)
            return Direction.DOWN;
        else if(dy < 0)
            return Direction.UP;
        else
            return null; //haha
    }

    Int2 findNextCrossroad(Float2 coordinates, Direction direction) {
        Int2 c1 = coordinates.toInt2(),
             c2;
        if(getFreeNeighbourCells(c1).size() > 2) return c1;
        Location l2;
        Direction d2;
        Set<Int2> neighbours;
        while(true) {
            c2 = direction.nextCell(c1);
            l2 = getLocation(c2);
            if(l2 != Location.WALL && l2 != null) {
                if(getFreeNeighbourCells(c2).size() > 2)
                    return c2;
                else
                    c1 = c2;
            } else {
                Log.d("findNextCrossroad", "angle branch");
                neighbours = getFreeNeighbourCells(c1);
                if(neighbours.size() == 1) {
                    direction = direction.opposite();
                } else {
                    for(Int2 n : getFreeNeighbourCells(c1)) {
                        d2 = findDirectionBetween(c1, n);
                        if(d2.opposite() != direction) {
                            direction = d2;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static double distance(Int2 c1, Int2 c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }

    public static double distance(Float2 c1, Float2 c2) {
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

    public Location getLocation(Int2 cell) {
        if(cell.x < 0 || cell.y < 0 || cell.x >= width || cell.y >= height)
            return null;
        else
            return getArray()[cell.x][cell.y];
    }

    public void setLocation(Int2 cell, Location value) {
        getArray()[cell.x][cell.y] = value;
    }
}