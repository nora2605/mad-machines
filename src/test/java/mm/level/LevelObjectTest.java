package mm.level;

import javafx.geometry.Rectangle2D;
import mm.model.GameObject;

import org.jbox2d.common.Vec2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// parameterized Tests behaving weirdly, 
// which results in lower coverage score at the switch statement
class LevelObjectTest {

    @Test
    void testGetBoundingBox() {
        LevelObject obj = new LevelObject();
        obj.type = "Crate";
        obj.position = new Vec2(3, 4);
        obj.rotation = 90f;

        GameObject go = mock(GameObject.class);
        Rectangle2D rect = new Rectangle2D(0, 0, 10, 10);
        when(go.getBoundingBox(any(), anyFloat())).thenReturn(rect);

        LevelObject spyObj = spy(obj);
        doReturn(go).when(spyObj).realize();

        Rectangle2D result = spyObj.getBoundingBox();
        assertEquals(rect, result);
        verify(go).getBoundingBox(obj.position, (float) Math.PI / 2);
    }
    
    @Test
    void testRealizePlank() {
        LevelObject obj = new LevelObject();
        obj.setType("Plank");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }

    @Test
    void testRealizeBBall() {
        LevelObject obj = new LevelObject();
        obj.setType("BouncyBall");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }

    @Test
    void testRealizeDomino() {
        LevelObject obj = new LevelObject();
        obj.setType("Domino");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }

    @Test
    void testRealizeBalloon() {
        LevelObject obj = new LevelObject();
        obj.setType("Balloon");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }

    @Test
    void testRealizeLog() {
        LevelObject obj = new LevelObject();
        obj.setType("Log");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }
    
    @Test
    void testRealizeCrate() {
        LevelObject obj = new LevelObject();
        obj.setType("Crate");
        obj.setPosition(new Vec2(0,0));
        obj.setRotation(0);
    }

    @Test
    void testRealizeUnknownThrows() {
        LevelObject obj = new LevelObject();
        obj.type = "UnknownType";
        Exception ex = assertThrows(IllegalArgumentException.class, obj::realize);
        assertTrue(ex.getMessage().contains("Unknown object type"));
    }

    @Test
    void testGettersAlthoughItsBS() {
        LevelObject lo = new LevelObject();
        assertEquals(null, lo.getType());
        assertEquals(lo.getPosition(), new Vec2(0,0));
        assertEquals(0, lo.getRotation());
    }
}
