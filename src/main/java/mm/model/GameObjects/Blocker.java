package mm.model.GameObjects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Blocker extends PhysicalGameObject {
    private static final float RADIUS = 0.05f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Blocker.png").toExternalForm());
    private final BodyDef bodyDef = new BodyDef();

    /**
     * Constructor for Blocker object
     */
    public Blocker() {
        bodyDef.type = BodyType.STATIC;
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(bodyDef);
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        return new Rectangle2D(position.x - RADIUS * 10, position.y - RADIUS * 10, 20 * RADIUS, 20 * RADIUS);
    }

    @Override
    public void render(GraphicsContext ctx, Vec2 position, float rotation) {
        float x = position.x;
        float y = position.y;

        ctx.translate(x, y);
        ctx.rotate(rotation);

        ctx.drawImage(sprite, -RADIUS, -RADIUS, 2 * RADIUS, 2 * RADIUS);
    
        ctx.rotate(-rotation);
        ctx.translate(-x, -y);
    }
}
