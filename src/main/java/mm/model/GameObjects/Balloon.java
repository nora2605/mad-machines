package mm.model.GameObjects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mm.model.PhysicalGameObject;
import mm.view.ResourceLoader;

public class Balloon extends PhysicalGameObject {

    // physics parameters
    private static final float RADIUS = 0.16f;
    private static final float RESTITUTION = 0.1f;
    private static final float DENSITY = 3.0f;
    private static final float FRICTION = 0.9f;

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/Balloon.png").toExternalForm());

    private final BodyDef bodyDef = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();

    /**
     * Constructor for Balloon object
     */
    public Balloon() {
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.gravityScale = -0.6f;

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        fixtureDef.shape = shape;
        fixtureDef.density = DENSITY;
        fixtureDef.friction = FRICTION;
        fixtureDef.restitution = RESTITUTION;
    }

    @Override
    public void instantiate(World world) {
        physicsObject = world.createBody(bodyDef);
        physicsObject.createFixture(fixtureDef);
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
