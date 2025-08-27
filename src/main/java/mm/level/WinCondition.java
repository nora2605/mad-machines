package mm.level;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.geometry.Rectangle2D;
import mm.model.SpecialObjects.WinSensor;

public class WinCondition {
    public String target;
    public Vec2 position;
    public Vec2 size;

    /**
     * Instantiates a WinCondition with the given target, position and size
     * @param world
     * @return
     */
    public WinSensor instantiate(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        bodyDef.position.set(position.add(size.mul(0.5f)));
        
        Body b = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2, size.y / 2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        b.createFixture(fixtureDef);

        return new WinSensor(target, b, position, size);
    }

    @JsonIgnore
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.x, position.y, size.x, size.y);
    }
}