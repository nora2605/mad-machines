package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import mm.level.LevelObject;
import mm.model.EditorObject;

class EditorObjectTest {
    @Test
    void testConstructor() {
        LevelObject lObj = new LevelObject();
        EditorObject eObj = new EditorObject(lObj);
        assertEquals(lObj, eObj.prototype);
        assertEquals(false, eObj.selected);
    }
}
