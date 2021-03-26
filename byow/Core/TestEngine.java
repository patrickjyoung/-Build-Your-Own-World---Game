package byow.Core;

import byow.TileEngine.TERenderer;

public class TestEngine {
    public static void main(String[] args) {
        Engine engine = new Engine();
        TERenderer ter = new TERenderer();
        ter.initialize(engine.WIDTH, engine.HEIGHT);
        engine.interactWithInputString("n7193300625454684331saaawasdaawd:q");
        engine.interactWithInputString("lwsd");
        System.out.println(engine.toString());
    }
}
