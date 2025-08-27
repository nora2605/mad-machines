package mm.level;

import javafx.geometry.Rectangle2D;
import mm.model.GameObject;
import mm.model.SpecialObject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LevelSpecialObjectTest {

    @Test
    void testRealizeUnknownReturnsNull() {
        LevelSpecialObject obj = new LevelSpecialObject();
        obj.type = "UnknownType";
        assertNull(obj.realize());
    }

    // still bloated from trying to get mockito working
    @Test
    void testGetBoundingBoxCallsSpecialObjectAndGameObjects() {
        LevelSpecialObject special = spy(new LevelSpecialObject());
        special.type = "Rope";
        special.children = new int[]{0, 1};

        GameLevel parent = mock(GameLevel.class);
        LevelObject objA = mock(LevelObject.class);
        LevelObject objB = mock(LevelObject.class);
        parent.objects = new LevelObject[]{objA, objB};

        GameObject goA = mock(GameObject.class);
        GameObject goB = mock(GameObject.class);
        when(objA.realize()).thenReturn(goA);
        when(objB.realize()).thenReturn(goB);

        SpecialObject so = mock(SpecialObject.class);
        Rectangle2D rect = new Rectangle2D(1, 2, 3, 4);
        when(so.getBoundingBox()).thenReturn(rect);
        doReturn(so).when(special).realize();

        Rectangle2D result = special.getBoundingBox(parent);

        verify(objA).realize();
        verify(objB).realize();
        verify(goA).instantiate(any());
        verify(goB).instantiate(any());
        verify(so).instantiate(any(), eq(goA), eq(goB));
        assertEquals(rect, result);
    }
}
