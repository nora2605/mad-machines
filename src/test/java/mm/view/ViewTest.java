package mm.view;

import org.jbox2d.common.Vec2;
import org.junit.jupiter.api.Test;

import mm.view.View;

import static org.junit.jupiter.api.Assertions.*;

class ViewTest {

    @Test
    void testDefaultConstructor() {
        View view = new View();
        assertEquals(0f, view.getLeft());
        assertEquals(1f, view.getRight());
        assertEquals(0f, view.getTop());
        assertEquals(1f, view.getBottom());
        assertEquals(1f, view.getWidth());
        assertEquals(1f, view.getHeight());
        assertEquals(0.5f, view.getCenterX());
        assertEquals(0.5f, view.getCenterY());
    }

    @Test
    void testCustomConstructor() {
        View view = new View(2f, 6f, 3f, 7f);
        assertEquals(2f, view.getLeft());
        assertEquals(6f, view.getRight());
        assertEquals(3f, view.getTop());
        assertEquals(7f, view.getBottom());
        assertEquals(4f, view.getWidth());
        assertEquals(4f, view.getHeight());
        assertEquals(4f, view.getCenterX());
        assertEquals(5f, view.getCenterY());
    }

    @Test
    void testFromExtent() {
        Vec2 pos = new Vec2(5, 10);
        Vec2 size = new Vec2(20, 30);
        View view = View.fromExtent(pos, size);
        assertEquals(5f, view.getLeft());
        assertEquals(25f, view.getRight());
        assertEquals(10f, view.getTop());
        assertEquals(40f, view.getBottom());
        assertEquals(20f, view.getWidth());
        assertEquals(30f, view.getHeight());
    }
}