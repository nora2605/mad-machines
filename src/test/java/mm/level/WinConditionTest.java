package mm.level;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.junit.jupiter.api.Test;

import mm.model.SpecialObjects.WinSensor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WinConditionTest {
    //TODO
    @Test
    void testInstantiateCreatesWinSensorWithCorrectFields() {
        WinCondition cond = new WinCondition();
        cond.target = "Ball";
        cond.position = new Vec2(1, 2);
        cond.size = new Vec2(3, 4);

        World world = mock(World.class);
        Body body = mock(Body.class);
        when(world.createBody(any(BodyDef.class))).thenReturn(body);

        WinSensor sensor = cond.instantiate(world);

        assertNotNull(sensor);
        assertEquals("Ball", sensor.target);
        assertEquals(cond.position, sensor.position);
        assertEquals(cond.size, sensor.size);
        assertEquals(body, sensor.body);
    }

    //TODO
    @Test
    void testInstantiateSetsBodyDefCorrectly() {
        WinCondition cond = new WinCondition();
        cond.target = "Box";
        cond.position = new Vec2(5, 6);
        cond.size = new Vec2(2, 2);

        World world = mock(World.class);
        Body body = mock(Body.class);
        when(world.createBody(any(BodyDef.class))).thenAnswer(invocation -> {
            BodyDef def = (BodyDef) invocation.getArguments()[0];
            assertEquals(BodyType.STATIC, def.type);
            // Should be position + size * 0.5f
            assertEquals(new Vec2(6, 7), def.position);
            return body;
        });

        cond.instantiate(world);
    }
}