package byow.Core;

import byow.TileEngine.TERenderer;

/**
 * Renders world created by Engine class.
 * @author Aleysha Chen, Patrick Young
 */
public class WorldRenderer {
    private Long seed;
    private TERenderer ter;
    private int width;
    private int height;
    public WorldRenderer(Long givenSeed, TERenderer givenTer, int givenWidth, int givenHeight) {
        this.seed = givenSeed;
        this.ter = givenTer;
        this.width = givenWidth;
        this.height = givenHeight;
    }

    /**
     * Uses given seed from Engine class to render world.
     */
    public void processInputSeed() {
        Engine engine = new Engine();
        ter = new TERenderer();
        /** ter.renderFrame(engine.interactWithInputString("n13106s")); */
    }
}
