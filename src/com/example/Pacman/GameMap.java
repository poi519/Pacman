package com.example.Pacman;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public Set<Map.Entry<Direction, int[]>> getFreeNeighbourCells(int x, int y) {
        Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
        Set<Map.Entry<Direction, int[]>> result = new HashSet<Map.Entry<Direction, int[]>>();
        for(Direction d : directions) {
            int[] c = d.nextCell(x, y);
            if(getLocation(c) != Location.WALL)
                result.add(new AbstractMap.SimpleEntry<Direction, int[]>(d, c));
        }
        return result;
    }

    public static double distance(int[] c1, int[] c2) {
        return Math.sqrt((c1[0] - c2[0]) ^ 2 + (c1[1] - c2[1]) ^ 2 );
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