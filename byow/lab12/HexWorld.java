package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    public void addHexagon(TETile[][] array, int size, int xpos, int ypos) {
        int start = xpos;
        for (int i = 0; i < size * 2; i++) {
            for (int j = 0; j < size + 2 * i; i++) {
                array[i + ypos][j + xpos] = Tileset.NOTHING;
            }
        }

    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];

      
    }
}
