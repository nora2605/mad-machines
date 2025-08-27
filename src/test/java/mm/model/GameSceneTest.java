package mm.model;

import org.junit.jupiter.api.Test;

import mm.level.GameLevel;
import mm.model.GameScene;

class GameSceneTest {

    //Wont work without jfx runtime
    @Test
    void testConstructor() {
        GameLevel level = GameLevel.fromJSON("{ \"name\": \"TestLevel\", \"description\": \"desc\", \"view\": null, \"objects\": [], \"special\": [], \"available\": [], \"winCondition\": null }");
        GameScene scene = new GameScene(level);
    }

}