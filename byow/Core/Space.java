package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class Space {
    private List<Coordinates> clist = new ArrayList<>();
    private int width;
    private int height;
    private Coordinates start;
    private Map<Coordinates, TETile> tileMap;
    private ArrayList<Coordinates> randomborder;

    public Space(Coordinates startpos, int width, int height) {
        this.width = width;
        this.height = height;
        this.start = startpos;
        tileMap = new HashMap<>();
        randomborder = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Coordinates test = new Coordinates(startpos.getX() + i, startpos.getY() + j);
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    tileMap.put(test, Tileset.WALL);
                    if (!((i == 0 && j == 0) || (i == 0 && j == height)
                            || (i == width && j == 0) || (i == width && j == height))) {
                        randomborder.add(test);
                    }
                } else {
                    tileMap.put(test, Tileset.FLOOR);
                }
                clist.add(test);
            }
        }
    }
    public List<Coordinates> getCoords() {
        return clist;
    }
    public Map<Coordinates, TETile> getMap() {
        return tileMap;
    }
    public ArrayList<Coordinates> getWalls() {
        return randomborder;
    }
    public Coordinates getStart() {
        return this.start;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
}
