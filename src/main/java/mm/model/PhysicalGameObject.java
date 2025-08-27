package mm.model;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract class for game objects that have a physical representation in the world.
 * This class provides default implementations for some methods in the GameObject interface.
 */
public abstract class PhysicalGameObject implements GameObject {
    protected Body physicsObject;

    @Override
    public Body getPhysicsObject() {
        return physicsObject;
    }

    @Override
    public void update(float deltaTime) {
        // Default implementation does nothing
    }

    @Override
    public void render(GraphicsContext ctx) {
        render(ctx, getPosition(), getRotation());
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return getBoundingBox(getPosition(), getRotation() * (float) Math.PI / 180.0f);
    }

    @Override
    public void onCollision(GameObject other) {
        // Default implementation does nothing
    }

    @Override
    public void setPosition(float x, float y) {
        physicsObject.setTransform(new Vec2(x, y), physicsObject.getAngle());
    }

    @Override
    public void setRotation(float angle) {
        physicsObject.setTransform(physicsObject.getPosition(), (float) Math.PI * angle / 180.0f);
    }

    @Override
    public Vec2 getPosition() {
        return physicsObject.getPosition();
    }

    @Override
    public float getRotation() {
        return physicsObject.getAngle() * 180.0f / (float) Math.PI;
    }

    @Override
    public void remove(World world) {
        if (physicsObject != null) {
            world.destroyBody(physicsObject);
            physicsObject = null;
        }
    }
}
