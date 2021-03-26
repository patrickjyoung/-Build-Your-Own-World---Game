package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

/**
 * Assembles all components of the world together to create
 * a 2D world of rooms and hallways.
 * @author Aleysha Chen, Patrick Young
 */
public class Assembly {
    private static final int MAX_SIDE = 8;
    private Map<Coordinates, Integer> library;
    private List<Space> spacelist;
    private int gwidth;
    private int gheight;
    private List<Coordinates> hallCoords;

    /**
     * Compiles all coordinate positions of all randomly constructed
     * rooms and connecting hallways.
     * @param width Integer type maximum width of given world
     * @param height Integer type maximum height of given world
     * @param rand Random type random generator
     */
    public Assembly(int width, int height, Random rand) {
        library = new HashMap<>();
        spacelist = new ArrayList<>();
        gwidth = width;
        gheight = height;
        int numberofrooms = RandomUtils.uniform(rand, 4, 20);
        while (numberofrooms != 0) {
            int xsize = RandomUtils.uniform(rand, 4, MAX_SIDE + 1);
            int ysize = RandomUtils.uniform(rand, 4, MAX_SIDE + 1);
            int xstartval = RandomUtils.uniform(rand, 1, width);
            int ystartval = RandomUtils.uniform(rand, 1, height);
            while (conditionchecker(xstartval, ystartval, xsize, ysize)) {
                xsize = RandomUtils.uniform(rand, 4, MAX_SIDE + 1);
                ysize = RandomUtils.uniform(rand, 4, MAX_SIDE + 1);
                xstartval = RandomUtils.uniform(rand, 1, width);
                ystartval = RandomUtils.uniform(rand, 1, height);
            }
            Coordinates start = new Coordinates(xstartval, ystartval);
            Space potential = new Space(start, xsize, ysize);
            spacelist.add(potential);
            for (Coordinates j : potential.getCoords()) {
                library.put(j, 1);
            }
            numberofrooms -= 1;
        }
        connectRoom(spacelist, spacelist.get(spacelist.size() - 1).getMap(),
                rand);
    }

    /**
     * Checks whether given coordinates overlaps with an existent room.
     * @param startxval Integer type x-coordinate of the origin of given room
     * @param startyval Integer type y-coordinate of the origin of given room
     * @param xsize Integer type width of given room
     * @param ysize Integer type height of given room
     * @return Boolean
     */
    private boolean conditionchecker(int startxval, int startyval,
                                     int xsize, int ysize) {
        if (xsize + startxval + 1 >= gwidth || ysize + startyval + 1
                >= gheight) {
            return true;
        }
        for (int i = 0; i < xsize; i++) {
            for (int j = 0; j < ysize; j++) {
                Coordinates test = new Coordinates(startxval + i,
                        startyval + j);
                if (library.containsKey(test)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates randomized hallways to connect each room in given list of rooms.
     * @param listOfSpace List type given list of rooms
     * @param mapOfTile Map type pairings between position coordinates of rooms
     *                  and hallways and corresponding type of tile
     * @param rand Random type random generator
     */
    public void connectRoom(List<Space> listOfSpace,
                             Map<Coordinates, TETile> mapOfTile, Random rand) {
        List<Coordinates> hallEndPoints = new ArrayList<>();
        hallCoords = new ArrayList<>();
        for (Space s : listOfSpace) {
            int startX = s.getStart().getX();
            int startY = s.getStart().getY();
            Map<Integer, Coordinates> sideMap = new HashMap<>();
            Coordinates xTop = new Coordinates(RandomUtils.uniform(rand, startX + 2,
                    startX + s.getWidth() - 1), startY);
            Coordinates xBottom = new Coordinates(RandomUtils.uniform(rand, startX + 2,
                    startX + s.getWidth() - 1), startY + s.getHeight() - 1);
            Coordinates yLeft = new Coordinates(startX, RandomUtils.uniform(rand, startY + 2,
                    startY + s.getHeight() - 1));
            Coordinates yRight = new Coordinates(startX + s.getWidth() - 1,
                    RandomUtils.uniform(rand, startY + 2, startY + s.getHeight() - 1));
            sideMap.put(1, xTop);
            sideMap.put(2, yLeft);
            sideMap.put(3, xBottom);
            sideMap.put(4, yRight);
            Coordinates hallPoint = sideMap.get(RandomUtils.uniform(rand, 1, 5));
            hallEndPoints.add(hallPoint);
            connectRoomHelper(hallEndPoints, mapOfTile);
        }
    }
    public void connectRoomHelper(List<Coordinates> listOfHallEndPoints,
                                  Map<Coordinates, TETile> mapOfTile) {
        for (int i = 0; i < listOfHallEndPoints.size() - 1; i += 1) {
            Coordinates hallStart = listOfHallEndPoints.get(i);
            Coordinates hallEnd = listOfHallEndPoints.get(i + 1);
            Coordinates pathPaver = hallStart;
            if ((pathPaver.getY() - hallEnd.getY()) > 0) {
                while (pathPaver.getY() != hallEnd.getY()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    if (pathPaver.getX() < 0 || pathPaver.getX() >= gwidth || pathPaver.getY() < 0
                            || pathPaver.getY() >= gheight) {
                        return;
                    } else {
                        hallCoords.add(pathPaver);
                        pathPaver = new Coordinates(pathPaver.getX(), pathPaver.getY() - 1);
                    }
                }
                if (pathPaver.getY() == hallEnd.getY()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    hallCoords.add(pathPaver);
                }
            } else if ((pathPaver.getY() - hallEnd.getY()) < 0) {
                while (pathPaver.getY() != hallEnd.getY()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    if (pathPaver.getX() < 0 || pathPaver.getX() >= gwidth || pathPaver.getY() < 0
                            || pathPaver.getY() >= gheight) {
                        return;
                    } else {
                        hallCoords.add(pathPaver);
                        pathPaver = new Coordinates(pathPaver.getX(), pathPaver.getY() + 1);
                    }
                }
                if (pathPaver.getY() == hallEnd.getY()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    hallCoords.add(pathPaver);
                }
            }
            if ((pathPaver.getX() - hallEnd.getX()) > 0) {
                while (pathPaver.getX() != hallEnd.getX()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    if (pathPaver.getX() < 0 || pathPaver.getX() >= gwidth
                            || pathPaver.getY() < 0 || pathPaver.getY() >= gheight) {
                        return;
                    } else {
                        hallCoords.add(pathPaver);
                        pathPaver = new Coordinates(pathPaver.getX() - 1, pathPaver.getY());
                    }
                }
                if (pathPaver.getX() == hallEnd.getX()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    hallCoords.add(pathPaver);
                }
            } else if ((pathPaver.getX() - hallEnd.getX()) < 0) {
                while (pathPaver.getX() != hallEnd.getX()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    if (pathPaver.getX() < 0 || pathPaver.getX() >= gwidth
                            || pathPaver.getY() < 0 || pathPaver.getY() >= gheight) {
                        return;
                    } else {
                        hallCoords.add(pathPaver);
                        pathPaver = new Coordinates(pathPaver.getX() + 1, pathPaver.getY());
                    }
                }
                if (pathPaver.getX() == hallEnd.getX()) {
                    mapOfTile.put(pathPaver, Tileset.FLOOR);
                    hallCoords.add(pathPaver);
                }
            }
        }
    }

    /**
     * Returns a list of created rooms and hallways.
     * @return List
     */
    public List<Space> getSpace() {
        return spacelist;
    }

    /**
     * Returns a list of position coordinates of tiles covered
     * by created hallways.
     * @return List
     */
    public List<Coordinates> getHallCoords() {
        return hallCoords;
    }
}
