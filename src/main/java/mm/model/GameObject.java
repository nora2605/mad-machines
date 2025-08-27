package mm.model;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface GameObject {
    /**
     * Provides a way to update internal state
     * @param deltaTime
     */
    public void update(float deltaTime);

    /**
     * Instantiates the object in the physics world
     * @param world
     */
    public default void instantiate(World world, Vec2 initialPosition, float initialRotation) {
        instantiate(world);
        setPosition(initialPosition.x, initialPosition.y);
        setRotation(initialRotation);
    }
    // legacy api
    public void instantiate(World world);

    /**
     * Gets the physics object associated with this game object
     * This is null if it's not been instantiated
     * @return
     */
    public Body getPhysicsObject();

    /**
     * Renders the object onto the canvas with supplied position and rotation
     * @param ctx
     * @param position
     * @param rotation
     */
    public void render(GraphicsContext ctx, Vec2 position, float rotation);

    /**
     * Renders the object onto the canvas in world coordinates
     * @param ctx
     */
    public void render(GraphicsContext ctx);

    /**
     * Gets the bounding box of the object in world coordinates
     * @return
     */
    public Rectangle2D getBoundingBox();
    /**
     * Gets the bounding box of the object in world coordinates with given position and rotation.
     * @param position
     * @param rotation
     * @return
     */
    public Rectangle2D getBoundingBox(Vec2 position, float rotation);

    /**
     * Called when the object collides with another object
     * @param other
     */
    public void onCollision(GameObject other);

    /**
     * Sets the position of the object in world coordinates
     * @return
     */
    public void setPosition(float x, float y);

    /**
     * Sets the rotation of the object in degrees
     * @return
     */
    public void setRotation(float angle);

    /**
     * Gets the position of the object in world coordinates
     * @return
     */
    public Vec2 getPosition();

    /**
     * Gets the rotation of the object în degrees
     * @return
     */
    public float getRotation();

    /**
     * String representation of the object
     */
    @Override
    public String toString();

    public void remove(World world);
}
