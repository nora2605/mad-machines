package mm.model.GameObjects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Log extends PhysicalGameObject {
    private static final float RADIUS = 0.09f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Log.png").toExternalForm());
    private final BodyDef bodyDef = new BodyDef();
    private final CircleShape shape;

    /**
     * Constructor for Log object
     */
    public Log() {
        bodyDef.type = BodyType.STATIC;
        shape = new CircleShape();
        shape.setRadius(RADIUS);
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(bodyDef);
        physicsObject.createFixture(shape, 0.0f);
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 position, float rotation) {
        return new Rectangle2D(position.x - RADIUS, position.y - RADIUS, 2 * RADIUS, 2 * RADIUS);
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
