package mm.model;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface SpecialObject {
    /**
     * Renders the special object on the given graphics context.
     *
     * @param ctx the graphics context to render on
     */
    void render(GraphicsContext ctx);

    /**
     * Updates the state of the special object.
     */
    public default void update(float deltaTime) { }

    /**
     * Renders a preview of the special object on the given graphics context.
     * @param ctx
     * @param posA
     * @param posB
     */
    void renderPreview(GraphicsContext ctx, Vec2 posA, Vec2 posB);

    /**
     * Instantiates the special object with 2 given GameObjects
     *
     * @param world the game world where the object will be instantiated
     */
    void instantiate(World world, GameObject objectA, GameObject objectB);

    /**
     * Returns the bounding box of the special object.
     *
     * @return the bounding box as a Rectangle2D
     */
    Rectangle2D getBoundingBox();

    /**
     * Returns the bounding box of the special object given its endpoints.
     *
     * @return the bounding box as a Rectangle2D
     */
    Rectangle2D getBoundingBox(Vec2 posA, Vec2 posB);

    /**
     * Removes itself from the world.
     * 
     */
    void remove(World world);
}
