package mm.level;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class GameLevelTest {

    @Test
    void testFromJSONValid() {
        String json = "{ \"name\": \"TestLevel\", \"description\": \"desc\", \"objects\": [], \"special\": [], \"available\": [], \"winCondition\": null, \"view\": null }";
        GameLevel level = GameLevel.fromJSON(json);
        assertNotNull(level);
        assertEquals("TestLevel", level.name);
        assertEquals("desc", level.description);
    }

    @Test
    void testFromJSONInvalid() {
        String invalidJson = "{ invalid json }";
        GameLevel level = GameLevel.fromJSON(invalidJson);
        assertNull(level);
    }

    @Test
    void testFromJSONforLevelCreatorValid() {
        String json = "{ \"name\": \"TestLevel\", \"description\": \"desc\", \"objects\": [], \"special\": [], \"available\": [], \"winCondition\": null, \"view\": null }";
        GameLevel level;
        try {
            level = GameLevel.fromJSONforLevelCreator(json);
            assertNotNull(level);
            assertEquals("TestLevel", level.name);
            assertEquals("desc", level.description);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Test
    void testFromJSONforLevelCreatorInvalid() {
        String invalidJson = "{ invalid json }";
        GameLevel level;
        try {
            level = GameLevel.fromJSONforLevelCreator(invalidJson);
            assertNull(level);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testToJSONAndFromJSON() {
        GameLevel level = new GameLevel();
        level.name = "Level1";
        level.description = "A test level";
        level.objects = new LevelObject[0];
        level.special = new LevelSpecialObject[0];
        level.available = new String[0];
        level.winCondition = null;
        level.view = null;

        String json = level.toJSON();
        assertNotNull(json);

        GameLevel parsed = GameLevel.fromJSON(json);
        assertNotNull(parsed);
        assertEquals(level.name, parsed.name);
        assertEquals(level.description, parsed.description);
    }
}