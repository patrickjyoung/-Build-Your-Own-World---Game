package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pseudorandomly generates world of rooms and hallways with given seed.
 * @author Aleysha Chen, Patrick Young
 */
public class Engine {
    private TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private List<Coordinates> wallCoords;
    private Coordinates avatarCoords;
    private boolean checker = false;
    private boolean multisave = false;
    private int health = 50;
    private int mask = 1;
    private int level = 0;
    private boolean stopRepeat = true;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT + 2);
        drawStartFrame();
        String userSeed = "n";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char current = StdDraw.nextKeyTyped();
                if (current == 'n' || current == 'N') {
                    for (int i = 0; i < 4; i++) {
                        File save = new File("save" + i + ".txt");
                        if (save.exists()) {
                            save.delete();
                        }
                    }
                    userSeed = userSeed + drawSeedFrame();
                    interactivity(userSeed);
                }
                if (current == 'l' || current == 'L') {
                    loadhelper(0);
                }
                if (current == 'q' || current == 'Q') {
                    System.exit(0);
                }
                if (current == 'o' || current == 'O') {
                    drawloadFrame();
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char savenum = StdDraw.nextKeyTyped();
                            if (savenum == '1' || savenum == '2' || savenum == '3') {
                                String s = "" + savenum;
                                loadhelper(Integer.parseInt(s));
                            }
                        }
                    }
                }
                if (current == 'r' || current == 'R') {
                    replay();
                }
            }
        }
    }

    private void replay() {
        File load = new File("save0.txt");
        if (!load.exists()) {
            System.exit(0);
        }
        String stringInput = IOUtils.read(load);
        String seed = stringInput.substring(0, stringInput.indexOf('s') + 1);
        stringInput = stringInput.substring(stringInput.indexOf('s') + 1, stringInput.length() - 2);
        if (stringInput == null) {
            System.exit(0);
        }
        replayhelp(seed, stringInput);
        interactivity(seed + stringInput);
    }
    private void replayhelp(String seed, String actions) {
        for (int i = 0; i < actions.length(); i++) {
            TETile[][] world = interactWithInputString(seed + actions.substring(0, i));
            ter.renderFrame(world, -1, -1, mask, health);
            StdDraw.pause(200);
        }
    }

    private void loadhelper(int i) {
        File load = new File("save" + i + ".txt");
        if (!load.exists()) {
            System.exit(0);
        }
        String stringInput = IOUtils.read(load);
        stringInput = stringInput.substring(0, stringInput.length() - 2);
        if (stringInput == null) {
            System.exit(0);
        }
        interactivity(stringInput);
    }

    private void interactivity(String input) {
        String userSeed = input;
        drawLevelFrame();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char code = StdDraw.nextKeyTyped();
                userSeed += code;
                if (code == 'm') {
                    mask += 1;
                }
                stopRepeat = true;
            }
            if (health == 0) {
                drawDeathFrame();
            }
            int mousex = (int) Math.floor(StdDraw.mouseX());
            int mousey = (int) Math.floor(StdDraw.mouseY());
            if (mousex >= WIDTH) {
                mousex = WIDTH - 1;
            } else if (mousex < 0) {
                mousex = 0;
            }
            if (mousey >= HEIGHT) {
                mousey = HEIGHT - 1;
            } else if (mousey < 0) {
                mousey = 0;
            }
            TETile[][] world = interactWithInputString(userSeed);
            ter.renderFrame(world, mousex, mousey, mask, health);
            if (checker) {
                System.exit(0);
            }
            if (multisave) {
                while (true) {
                    drawMenuFrame();
                    if (StdDraw.hasNextKeyTyped()) {
                        char code = StdDraw.nextKeyTyped();
                        if (code == '1' || code == '2' || code == '3') {
                            String s = "" + code;
                            saveHelp(Integer.parseInt(s), input);
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        String ninput = input.toLowerCase();
        char startInput = ninput.charAt(0);
        if (!(startInput == 'n')) {
            if (startInput == 'l') {
                return loadHelperNotRendered(ninput.substring(1));
            } else if (startInput == 'q') {
                return null;
            }
        }
        int endIndex = ninput.indexOf('s');
        char endInput = ninput.charAt(endIndex);
        if (!(endInput == 's')) {
            throw new IllegalArgumentException();
        }
        String rinput = input.substring(1, endIndex);
        long seed = Long.parseLong(rinput);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                finalWorldFrame[i][j] = Tileset.NOTHING;
            }
        }
        Random prandgen = new Random(seed);
        Assembly compiler = new Assembly(WIDTH, HEIGHT, prandgen);
        List<Space> shapes = compiler.getSpace();
        this.wallCoords = new ArrayList<>();
        for (Space s : shapes) {
            addShape(s, finalWorldFrame);
        }
        for (Coordinates h : compiler.getHallCoords()) {
            addHall(h, finalWorldFrame);
            addWall(h, finalWorldFrame);
        }
        List<Coordinates> floors = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (finalWorldFrame[i][j] == Tileset.FLOOR) {
                    floors.add(new Coordinates(i, j));
                }
                if (finalWorldFrame[i][j] == Tileset.WALL
                        && avatarChecker(i, j, finalWorldFrame)) {
                    this.wallCoords.add(new Coordinates(i, j));
                }
            }
        }
        for (int i = 0; i < RandomUtils.uniform(prandgen, 2 * level, 6 * level + 1); i++) {
            Coordinates coords = floors.remove(RandomUtils.uniform(prandgen, 0, floors.size()));
            finalWorldFrame[coords.getX()][coords.getY()] = Tileset.SAND;
        }
        if (wallCoords.size() != 0) {
            int entrancePos = RandomUtils.uniform(prandgen, 0, wallCoords.size());
            int exitPos = RandomUtils.uniform(prandgen, 0, wallCoords.size());
            if (entrancePos == exitPos) {
                exitPos = entrancePos + 1;
            }
            addEntrance(wallCoords.get(entrancePos), finalWorldFrame);
            addAvatar(wallCoords.get(entrancePos), finalWorldFrame);
            addExit(wallCoords.get(exitPos), finalWorldFrame);
        }
        String directions = ninput.substring(endIndex + 1);
        for (int i = 0; i < directions.length(); i++) {
            char c = directions.charAt(i);
            if (i > 0) {
                if (c == 'q' && directions.charAt(i - 1) == ':') {
                    saveHelp(0, input);
                    return finalWorldFrame;
                }
                if (c == 'm' && directions.charAt(i - 1) == ':') {
                    multisave = true;
                    return finalWorldFrame;
                }
            }
            if (c == 'a') {
                checkExit(prandgen, avatarCoords, finalWorldFrame, -1, 0);
                avatarCoords = moveAvatar(avatarCoords, finalWorldFrame, -1, 0);
            } else if (c == 's') {
                checkExit(prandgen, avatarCoords, finalWorldFrame, 0, -1);
                avatarCoords = moveAvatar(avatarCoords, finalWorldFrame, 0, -1);
            } else if (c == 'd') {
                checkExit(prandgen, avatarCoords, finalWorldFrame, 1, 0);
                avatarCoords = moveAvatar(avatarCoords, finalWorldFrame, 1, 0);
            } else if (c == 'w') {
                checkExit(prandgen, avatarCoords, finalWorldFrame, 0, 1);
                avatarCoords = moveAvatar(avatarCoords, finalWorldFrame, 0, 1);
            }
        }
        return finalWorldFrame;
    }

    private TETile[][] loadHelperNotRendered(String input) {
        File load = new File("save0.txt");
        if (!load.exists()) {
            return null;
        }
        String stringInput = IOUtils.read(load);
        stringInput = stringInput.substring(0, stringInput.length() - 2);
        if (stringInput == null) {
            return null;
        }
        return interactWithInputString(stringInput + input);
    }

    private boolean avatarChecker(int x, int y, TETile[][]world) {
        if (x >= WIDTH - 1 || x < 1) {
            return false;
        }
        if (y >= HEIGHT - 1 || y < 1) {
            return false;
        }
        TETile right = world[x + 1][y];
        TETile left = world[x - 1][y];
        TETile up = world[x][y + 1];
        TETile down = world[x][y - 1];
        return right == Tileset.FLOOR || left == Tileset.FLOOR
                || up == Tileset.FLOOR || down == Tileset.FLOOR;
    }

    private void saveHelp(int i, String input) {
        File newsave = new File("save" + i + ".txt");
        if (newsave.exists()) {
            newsave.delete();
        }
        try {
            newsave.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String writing = input;
        IOUtils.write(newsave, writing);
        checker = true;
    }

    private void checkExit(Random prandgen, Coordinates current, TETile[][] world, int dx, int dy) {
        if (world[current.getX() + dx][current.getY() + dy] == Tileset.LOCKED_DOOR) {
            String newinput = "n";
            for (int w = 0; w < RandomUtils.uniform(prandgen, 1, 10); w++) {
                newinput = newinput + RandomUtils.uniform(prandgen, 1, 10);
            }
            newinput = newinput + "s";
            level += 1;
            interactivity(newinput);
        }
    }

    private Coordinates moveAvatar(Coordinates current, TETile[][]world, int dx, int dy) {
        if ((world[current.getX() + dx][current.getY() + dy] == Tileset.FLOOR)) {
            world[current.getX() + dx][current.getY() + dy] = Tileset.AVATAR;
            world[current.getX()][current.getY()] = Tileset.FLOOR;
            return new Coordinates(current.getX() + dx, current.getY() + dy);
        }
        if ((world[current.getX() + dx][current.getY() + dy] == Tileset.SAND)) {
            world[current.getX() + dx][current.getY() + dy] = Tileset.AVATAR;
            world[current.getX()][current.getY()] = Tileset.FLOOR;
            if (mask % 2 == 1 && stopRepeat) {
                health -= 1;
                stopRepeat = false;
            }
            return new Coordinates(current.getX() + dx, current.getY() + dy);
        }
        return current;
    }

    private void addAvatar(Coordinates a, TETile[][]world) {
        TETile right = world[a.getX() + 1][a.getY()];
        TETile left = world[a.getX() - 1][a.getY()];
        TETile up = world[a.getX()][a.getY() + 1];
        TETile down = world[a.getX()][a.getY() - 1];
        if (right == Tileset.FLOOR) {
            world[a.getX() + 1][a.getY()] = Tileset.AVATAR;
            avatarCoords = new Coordinates(a.getX() + 1, a.getY());
        } else if (left == Tileset.FLOOR) {
            world[a.getX() - 1][a.getY()] = Tileset.AVATAR;
            avatarCoords = new Coordinates(a.getX() - 1, a.getY());
        } else if (up == Tileset.FLOOR) {
            world[a.getX()][a.getY() + 1] = Tileset.AVATAR;
            avatarCoords = new Coordinates(a.getX(), a.getY() + 1);
        } else if (down == Tileset.FLOOR) {
            world[a.getX()][a.getY() - 1] = Tileset.AVATAR;
            avatarCoords = new Coordinates(a.getX(), a.getY() - 1);
        }
    }

    private void addEntrance(Coordinates enter, TETile[][]world) {
        if (world[enter.getX()][enter.getY()] == Tileset.WALL) {
            world[enter.getX()][enter.getY()] = Tileset.UNLOCKED_DOOR;
        }
    }

    private void addExit(Coordinates exit, TETile[][]world) {
        if (world[exit.getX()][exit.getY()] == Tileset.WALL) {
            world[exit.getX()][exit.getY()] = Tileset.LOCKED_DOOR;
        }
    }

    /**
     * Uses given list of position coordinates of rooms to construct rooms
     * in world.
     * @param s Space type room created for insertion in world.
     * @param world Array of TETile type array representation of
     *              position coordinates of room tiles in world.
     */
    private void addShape(Space s, TETile[][]world) {
        List<Coordinates> coords = s.getCoords();
        for (Coordinates pos : coords) {
            world[pos.getX()][pos.getY()] = s.getMap().get(pos);
        }
    }

    /**
     * Uses given list of position coordinates of hallways to construct
     * hallways in world.
     * @param h Coordinates type representation of position
     *          coordinates of tiles covered by created hallways in world.
     * @param world Array of TETile type array representation of
     *              position coordinates of hallway tiles in world.
     */
    private void addHall(Coordinates h, TETile[][]world) {
        world[h.getX()][h.getY()] = Tileset.FLOOR;
    }

    /**
     * Uses given list of position coordinates of hallways to
     * line created hallways with walls in world.
     * @param h Coordinates type representation of position
     *          coordinates of tiles covered by created hallways in world.
     * @param world Array of TETile type array representation of
     *              position coordinates of hallway tiles in world.
     */
    public void addWall(Coordinates h, TETile[][]world) {
        TETile right = world[h.getX() + 1][h.getY()];
        TETile left = world[h.getX() - 1][h.getY()];
        TETile up = world[h.getX()][h.getY() + 1];
        TETile down = world[h.getX()][h.getY() - 1];
        if (right != Tileset.FLOOR) {
            world[h.getX() + 1][h.getY()] = Tileset.WALL;
        }
        if (left != Tileset.FLOOR) {
            world[h.getX() - 1][h.getY()] = Tileset.WALL;
        }
        if (up != Tileset.FLOOR) {
            world[h.getX()][h.getY() + 1] = Tileset.WALL;
        }
        if (down != Tileset.FLOOR) {
            world[h.getX()][h.getY() - 1] = Tileset.WALL;
        }
    }
    public void drawStartFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 26);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 1, "CS61B: The Game");
        Font fontSmall = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Other Saves (O)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Replay (R)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Quit (Q)");
        StdDraw.show();
    }
    private void drawloadFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 26);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 1, "Select Your Save");
        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 5, "1 - " + isSave(1));
        StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 10, "2 - " + isSave(2));
        StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 15, "3 - " + isSave(3));
        StdDraw.show();
    }
    private String isSave(int i) {
        File save = new File("save" + i + ".txt");
        if (!save.exists()) {
            return "Empty Save Slot";
        } else {
            return "Occupied Slot    ";
        }
    }
    private void drawDeathFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, 38);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "GAME OVER");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "PLEASE WEAR A MASK <3");
        StdDraw.show();
        StdDraw.pause(2000);
        System.exit(0);
    }
    private void drawMenuFrame() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT / 2 - 1, WIDTH / 4, HEIGHT / 4);
        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "Save States");
        StdDraw.text(WIDTH * 2 / 6, HEIGHT / 2 - 3, "1");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "2");
        StdDraw.text(WIDTH * 4 / 6, HEIGHT / 2 - 3, "3");
        StdDraw.show();
    }
    private void drawLevelFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, 38);
        StdDraw.setFont(fontSmall);
        if (level == 0) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "TUTORIAL LEVEL");
            Font fontSmaller = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmaller);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "You can only save and load on this tutorial level, like real life COVID, you only have one save file.");
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Get to level 10 and you win!");
        } else {
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "LEVEL " + level);
        }
        StdDraw.show();
        if (level == 0) {
            StdDraw.pause(2000);
        }
        StdDraw.pause(1750);
    }
    public String drawSeedFrame() {
        String userSeed = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font fontBig = new Font("Monaco", Font.BOLD, 26);
            StdDraw.setFont(fontBig);
            StdDraw.text(WIDTH / 2, HEIGHT * 5.0 / 6.0 - 1, "Please enter a seed followed by S");
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, userSeed);
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char code = StdDraw.nextKeyTyped();
                userSeed = userSeed + code;
                if (code == 'S' || code == 's') {
                    break;
                }
            }
        }
        return userSeed;
    }
}
